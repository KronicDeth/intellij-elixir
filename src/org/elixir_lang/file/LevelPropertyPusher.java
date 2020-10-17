package org.elixir_lang.file;

import com.intellij.ProjectTopics;
import com.intellij.diagnostic.PerformanceWatcher;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.DumbModeTask;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.FilePropertyPusher;
import com.intellij.openapi.roots.impl.PushedFilePropertiesUpdater;
import com.intellij.openapi.roots.impl.PushedFilePropertiesUpdaterImpl;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.openapi.vfs.newvfs.FileAttribute;
import com.intellij.util.FileContentUtil;
import com.intellij.util.io.DataInputOutputUtil;
import com.intellij.util.messages.MessageBus;
import gnu.trove.THashSet;
import org.elixir_lang.Level;
import org.elixir_lang.module.ElixirModuleType;
import org.elixir_lang.sdk.elixir.Release;
import org.elixir_lang.sdk.elixir.Type;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import static org.elixir_lang.Level.KEY;
import static org.elixir_lang.Level.MAXIMUM;
import static org.elixir_lang.sdk.elixir.Type.mostSpecificSdk;

public class LevelPropertyPusher implements FilePropertyPusher<Level> {
    private static final Key<Level> LEVEL = Key.create("ELIXIR_LEVEL");
    public static final Key<VirtualFile> VIRTUAL_FILE = Key.create("VIRTUAL_FILE");
    private static final FileAttribute PERSISTENCE = new FileAttribute("elixir_level_persistence", 1, true);
    private final Map<Module, Sdk> sdkByModule = new WeakHashMap<>();

    private static void putLevel(@NotNull Project project) {
        project.putUserData(LEVEL, computeLevel(project));
    }

    public static Level level(@Nullable Sdk sdk) {
        Level level;

        if (sdk != null) {
            level = sdk.getUserData(LEVEL);

            if (level == null) {
                level = computeLevel(sdk);

                Application application = ApplicationManager.getApplication();

                // MockSdk is not write-able
                if (application == null || !application.isUnitTestMode()) {
                    sdk.putUserData(LEVEL, level);
                }
            }
        } else {
            level = MAXIMUM;
        }

        return level;
    }

    public static Level level(@NotNull Project project) {
        Level level = project.getUserData(LEVEL);

        if (level == null) {
            level = computeLevel(project);
            project.putUserData(LEVEL, level);
        }

        return level;
    }

    public static Level level(@NotNull Module module) {
        Level level = module.getUserData(LEVEL);

        if (level == null) {
            level = computeLevel(module);
            module.putUserData(LEVEL, level);
        }

        return level;
    }

    public static Level level(@NotNull Project project, @Nullable VirtualFile virtualFile) {
        Level level = null;

        if (virtualFile != null) {
            level = virtualFile.getUserData(LEVEL);
        }

        if (level == null) {
            level = computeLevel(project, virtualFile);

            if (virtualFile != null) {
                virtualFile.putUserData(LEVEL, level);
            }
        }

        return level;
    }

    @NotNull
    private static Level computeLevel(@NotNull Module module) {
        Level level;

        Sdk sdk = mostSpecificSdk(module);

        if (sdk != null) {
            level = level(sdk);
        } else {
            level = MAXIMUM;
        }

        return level;
    }

    @NotNull
    private static Level computeLevel(@NotNull Project project, @Nullable VirtualFile virtualFile) {
        Level level;

        if (virtualFile == null) {
            level = level(project);
        } else {
            Module module = ModuleUtilCore.findModuleForFile(virtualFile, project);

            if (module != null) {
                level = level(module);
            } else {
                level = level(project);
            }
        }

        return level;
    }

    @NotNull
    private static Level computeLevel(@NotNull Project project) {
        Level level;

        final ModuleManager moduleManager = ModuleManager.getInstance(project);
        Level projectLevel = MAXIMUM;

        if (moduleManager != null) {
            Level maxLevel = null;

            for (Module module : moduleManager.getModules()) {
                final Sdk moduleSdk = ModuleRootManager.getInstance(module).getSdk();

                if (moduleSdk != null) {
                    final Level sdkLevel = level(moduleSdk);

                    if (maxLevel == null || maxLevel.ordinal() < sdkLevel.ordinal()) {
                        maxLevel = sdkLevel;
                    }
                }
            }

            if (maxLevel != null) {
                projectLevel = maxLevel;
            } else {
                Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
                projectLevel = computeLevel(projectSdk);
            }
        }

        level = projectLevel;

        return level;
    }

    @NotNull
    @Contract(pure = true)
    private static Level computeLevel(@Nullable Sdk sdk) {
        Release release = Type.getRelease(sdk);
        Level level;

        if (release != null) {
            level = release.level();
        } else {
            level = MAXIMUM;
        }

        return level;
    }

    private static Optional<Integer> levelOrdinal(@NotNull VirtualFile fileOrDirectory) throws IOException {
        DataInputStream dataInputStream = PERSISTENCE.readAttribute(fileOrDirectory);
        Optional<Integer> levelOrdinal = Optional.empty();

        if (dataInputStream != null) {
            try {
                levelOrdinal = Optional.of(DataInputOutputUtil.readINT(dataInputStream));
            } finally {
                dataInputStream.close();
            }
        }

        return levelOrdinal;
    }

    private static void deleteLevel(@NotNull Project project) {
        project.putUserData(LEVEL, null);
    }

    @Override
    public void initExtra(@NotNull Project project, @NotNull MessageBus bus, @NotNull Engine languageLevelUpdater) {
        Map<Module, Sdk> sdkByModule = sdkByModule(project);
        Set<Sdk> sdkSet = sdkByModuleToSdkSet(sdkByModule);

        this.sdkByModule.putAll(sdkByModule);
        deleteLevel(project);
        updateSdkLevels(project, sdkSet);
        putLevel(project);
    }

    private Map<Module, Sdk> sdkByModule(@NotNull Project project) {
        final Map<Module, Sdk> sdkByModule = new LinkedHashMap<>();
        final Module[] modules = ModuleManager.getInstance(project).getModules();

        for (Module module : modules) {
            ModuleType moduleType = ModuleType.get(module);

            if (moduleType instanceof ElixirModuleType) {
                Sdk sdk = ModuleRootManager.getInstance(module).getSdk();

                if (sdk != null && sdk.getSdkType() instanceof Type) {
                    sdkByModule.put(module, sdk);
                }
            }
        }

        return sdkByModule;
    }

    private void updateSdkLevels(@NotNull Project project, @NotNull Set<Sdk> sdkSet) {
        final DumbService dumbService = DumbService.getInstance(project);
        final DumbModeTask task = new DumbModeTask() {
            @Override
            public void performInDumbMode(@NotNull ProgressIndicator indicator) {
                if (!project.isDisposed()) {
                    final PerformanceWatcher.Snapshot snapshot = PerformanceWatcher.takeSnapshot();
                    final List<Runnable> tasks = ReadAction.compute(() -> rootUpdateTaskList(project, sdkSet));
                    PushedFilePropertiesUpdaterImpl.invokeConcurrentlyIfPossible(tasks);

                    if (!ApplicationManager.getApplication().isUnitTestMode()) {
                        snapshot.logResponsivenessSinceCreation(
                                "Pushing Elixir language level to " + tasks.size() + " roots in " +
                                        sdkSet.size() + " SDKs");
                    }
                }
            }
        };

        project.getMessageBus().connect(task).subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
            @Override
            public void rootsChanged(ModuleRootEvent event) {
                DumbService.getInstance(project).cancelTask(task);
            }
        });

        dumbService.queueTask(task);
    }

    private List<Runnable> rootUpdateTaskList(@NotNull Project project, @NotNull Set<Sdk> sdkSet) {
        final List<Runnable> rootUpdateTaskList = new ArrayList<>();

        for (Sdk sdk : sdkSet) {
            final Level level = level(sdk);

            for (VirtualFile root : sdk.getRootProvider().getFiles(OrderRootType.CLASSES)) {
                if (root.isValid()) {
                    rootUpdateTaskList.add(new UpdateRootTask(project, root, level));
                }
            }
        }

        return rootUpdateTaskList;
    }

    @NotNull
    @Override
    public Key<Level> getFileDataKey() {
        return KEY;
    }

    @Override
    public boolean pushDirectoriesOnly() {
        return true;
    }

    @NotNull
    @Override
    public Level getDefaultValue() {
        return MAXIMUM;
    }

    @Nullable
    @Override
    public Level getImmediateValue(@NotNull Project project, @Nullable VirtualFile file) {
        return level(project, file);
    }

    @Nullable
    @Override
    public Level getImmediateValue(@NotNull Module module) {
        return level(module);
    }

    @Override
    public boolean acceptsDirectory(@NotNull VirtualFile file, @NotNull Project project) {
        return true;
    }

    @Override
    public void persistAttribute(@NotNull Project project, @NotNull VirtualFile fileOrDir, @NotNull Level level)
            throws IOException {
        Optional<Integer> oldLevelOrdinal = levelOrdinal(fileOrDir);

        if (!oldLevelOrdinal.isPresent() || oldLevelOrdinal.get() == level.ordinal()) {
            DataOutputStream dataOutputStream = PERSISTENCE.writeAttribute(fileOrDir);
            DataInputOutputUtil.writeINT(dataOutputStream, level.ordinal());
            dataOutputStream.close();
        }
    }

    @Override
    public void afterRootsChanged(@NotNull Project project) {
        Map<Module, Sdk> sdkByModule = sdkByModule(project);
        Set<Sdk> sdkSet = sdkByModuleToSdkSet(sdkByModule);
        boolean needToReparseOpenFiles = false;

        for (Map.Entry<Module, Sdk> entry: sdkByModule.entrySet()) {
            Module module = entry.getKey();
            Sdk newSdk = entry.getValue();
            Sdk oldSdk = this.sdkByModule.get(module);

            if (this.sdkByModule.containsKey(module) && (newSdk != null || oldSdk != null) && newSdk != oldSdk) {
                needToReparseOpenFiles = true;
                break;
            }
        }

        this.sdkByModule.putAll(sdkByModule);
        deleteLevel(project);
        updateSdkLevels(project, sdkSet);

        if (needToReparseOpenFiles) {
            reparseFiles(project);
        }
    }

    private void reparseFiles(@NotNull Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (project.isDisposed()) {
                return;
            }

            FileContentUtil.reparseFiles(project, Collections.emptyList(), true);
        });
    }

    private Set<Sdk> sdkByModuleToSdkSet(@NotNull Map<Module, Sdk> sdkByModule) {
        Set<Sdk> sdkSet = new THashSet<>();

        for (@Nullable Sdk sdk : sdkByModule.values()) {
            if (sdk != null) {
                sdkSet.add(sdk);
            }
        }

        return sdkSet;
    }

    private final class UpdateRootTask implements Runnable {
        @NotNull
        private final Project project;
        @NotNull
        private final VirtualFile root;
        @NotNull
        private final Level level;

        UpdateRootTask(@NotNull Project project, @NotNull VirtualFile root, @NotNull Level level) {
            this.project = project;
            this.root = root;
            this.level = level;
        }

        @Override
        public void run() {
            if (project.isDisposed() || !ReadAction.compute(root::isValid)) return;

            final FileTypeManager fileTypeManager = FileTypeManager.getInstance();
            final PushedFilePropertiesUpdater propertiesUpdater = PushedFilePropertiesUpdater.getInstance(project);

            VfsUtilCore.visitChildrenRecursively(root, new VirtualFileVisitor() {
                @Override
                public boolean visitFile(@NotNull VirtualFile file) {

                    return ReadAction.compute(() -> {
                        boolean proceedToChildren;

                        if (fileTypeManager.isFileIgnored(file)) {
                            proceedToChildren = false;
                        } else {
                            proceedToChildren = true;

                            if (file.isDirectory()) {
                                propertiesUpdater.findAndUpdateValue(file, LevelPropertyPusher.this, level);
                            }
                        }

                        return proceedToChildren;
                    });
                }
            });
        }

        @Contract(pure = true)
        @NotNull
        @Override
        public String toString() {
            return "UpdateRootTask{" + "root=" + root + ", level=" + level + '}';
        }
    }
}
