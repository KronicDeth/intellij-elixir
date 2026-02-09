package org.elixir_lang.jps.builder;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileSystemUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.UsefulTestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.api.CanceledStatus;
import org.jetbrains.jps.builders.impl.BuildDataPathsImpl;
import org.jetbrains.jps.builders.impl.BuildRootIndexImpl;
import org.jetbrains.jps.builders.impl.BuildTargetIndexImpl;
import org.jetbrains.jps.builders.impl.BuildTargetRegistryImpl;
import org.jetbrains.jps.builders.logging.BuildLoggingManager;
import org.jetbrains.jps.builders.storage.BuildDataPaths;
import org.jetbrains.jps.cmdline.ProjectDescriptor;
import org.jetbrains.jps.incremental.BuilderRegistry;
import org.jetbrains.jps.incremental.IncProjectBuilder;
import org.jetbrains.jps.incremental.RebuildRequestedException;
import org.jetbrains.jps.incremental.fs.BuildFSState;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.relativizer.PathRelativizerService;
import org.jetbrains.jps.incremental.storage.BuildDataManager;
import org.jetbrains.jps.incremental.storage.BuildTargetsState;
import org.jetbrains.jps.incremental.storage.ProjectStamps;
import org.jetbrains.jps.indices.ModuleExcludeIndex;
import org.jetbrains.jps.indices.impl.IgnoredFileIndexImpl;
import org.jetbrains.jps.indices.impl.ModuleExcludeIndexImpl;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.java.*;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.library.sdk.JpsSdkReference;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsSdkReferencesTable;
import org.jetbrains.jps.util.JpsPathUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zyuyou on 15/7/17.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class JpsBuildTestCase extends UsefulTestCase {
    private static final Logger logger = Logger.getInstance(JpsBuildTestCase.class);

    private File myProjectDir;
    private TestProjectBuilderLogger myLogger;
    @Nullable
    private BuildResult myLastBuildResult;

    protected JpsProject myProject;
    protected JpsModel myModel;
    protected File myDataStorageRoot;

    protected Map<String, String> myBuildParams;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        myModel = JpsElementFactory.getInstance().createModel();
        myProject = myModel.getProject();
        myDataStorageRoot = FileUtil.createTempDirectory("compile-server-" + getProjectName(), null);
        myLogger = new TestProjectBuilderLogger();
        myLastBuildResult = null;
        myBuildParams = new HashMap<>();
    }

    @Override
    protected void tearDown() throws Exception {
        myProjectDir = null;
        super.tearDown();
    }

    protected static void change(String filePath) {
        change(filePath, null);
    }

    protected static void change(String filePath, @Nullable String newContent) {
        try {
            File file = new File(FileUtil.toSystemDependentName(filePath));
            assertTrue("File " + file.getAbsolutePath() + " doesn't exist", file.exists());
            if (newContent != null) {
                FileUtil.writeToFile(file, newContent);
            }

            long oldTimeStamp = FileSystemUtil.lastModified(file);
            long time = System.currentTimeMillis();
            setLastModified(file, time);
            if (FileSystemUtil.lastModified(file) <= oldTimeStamp) {
                setLastModified(file, time + 1);
                long newTimeStamp = FileSystemUtil.lastModified(file);
                if (newTimeStamp <= oldTimeStamp) {
                    // Mac OS and som versions of Linux truncates timestamp to nearest second
                    setLastModified(file, time + 1000);
                    newTimeStamp = FileSystemUtil.lastModified(file);
                    assertTrue("Failed to change timestamp for " + file.getAbsolutePath(), newTimeStamp > oldTimeStamp);
                }
                sleepUntil(newTimeStamp);
            }

        } catch (IOException e) {
            logger.warn("Failed to update file for test: " + filePath, e);
        }
    }

    protected static void sleepUntil(long time) {
        // we need this to ensure that the file won't be treated as changed by user during complication
        // and therefore marked for recompilation
        long delta;
        while ((delta = time - System.currentTimeMillis()) > 0) {
            try {
                // noinspection BusyWait
                Thread.sleep(delta);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static void setLastModified(File file, long time) {
        boolean updated = file.setLastModified(time);
        assertTrue("Cannot modify timestamp for " + file.getAbsolutePath(), updated);
    }

    protected String getProjectName() {
        return StringUtil.decapitalize(StringUtil.trimStart(getName(), "test"));
    }

    protected ProjectDescriptor createProjectDescriptor(BuildLoggingManager buildLoggingManager) {
        try {
            BuildTargetRegistryImpl targetRegistry = new BuildTargetRegistryImpl(myModel);
            ModuleExcludeIndex index = new ModuleExcludeIndexImpl(myModel);
            IgnoredFileIndexImpl ignoredFileIndex = new IgnoredFileIndexImpl(myModel);
            @SuppressWarnings("removal")
            BuildDataPaths dataPaths = new BuildDataPathsImpl(myDataStorageRoot);
            BuildRootIndexImpl buildRootIndex = new BuildRootIndexImpl(targetRegistry, myModel, index, dataPaths, ignoredFileIndex);
            BuildTargetIndexImpl targetIndex = new BuildTargetIndexImpl(targetRegistry, buildRootIndex);
            @SuppressWarnings("deprecation") BuildTargetsState targetsState = new BuildTargetsState(dataPaths, myModel, buildRootIndex);
            PathRelativizerService relativizer = new PathRelativizerService(myModel.getProject());
            @SuppressWarnings("removal") ProjectStamps timestamps = new ProjectStamps(myDataStorageRoot, targetsState, relativizer);
            BuildDataManager dataManager = new BuildDataManager(dataPaths, targetsState, relativizer);
            return new ProjectDescriptor(
                    myModel,
                    new BuildFSState(true),
                    timestamps,
                    dataManager,
                    buildLoggingManager,
                    index,
                    targetIndex,
                    buildRootIndex,
                    ignoredFileIndex
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T extends JpsElement> void addModule(
            String moduleName, String[] srcPaths, @Nullable String outputPath, @Nullable String testOutputPath,
            JpsSdk<T> sdk) {

        JpsModule module = myProject.addModule(moduleName, org.elixir_lang.jps.builder.model.ModuleType.INSTANCE);
        JpsSdkType<T> sdkType = sdk.getSdkType();
        JpsSdkReferencesTable sdkTable = module.getSdkReferencesTable();
        sdkTable.setSdkReference(sdkType, sdk.createReference());

        if (sdkType instanceof JpsJavaSdkTypeWrapper) {
            JpsSdkReference<T> wrapperRef = sdk.createReference();
            sdkTable.setSdkReference(JpsJavaSdkType.INSTANCE, JpsJavaExtensionService.getInstance().createWrappedJavaSdkReference((JpsJavaSdkTypeWrapper) sdkType, wrapperRef));
        }

        module.getDependenciesList().addSdkDependency(sdkType);
        if (srcPaths.length > 0 || outputPath != null) {
            for (String srcPath : srcPaths) {
                module.getContentRootsList().addUrl(JpsPathUtil.pathToUrl(srcPath));
                module.addSourceRoot(JpsPathUtil.pathToUrl(srcPath), JavaSourceRootType.SOURCE);
            }
            JpsJavaModuleExtension extension = JpsJavaExtensionService.getInstance().getOrCreateModuleExtension(module);
            if (outputPath != null) {
                extension.setOutputUrl(JpsPathUtil.pathToUrl(outputPath));
                if (!StringUtil.isEmpty(testOutputPath)) {
                    extension.setTestOutputUrl(JpsPathUtil.pathToUrl(testOutputPath));
                } else {
                    extension.setTestOutputUrl(extension.getOutputUrl());
                }

            } else {
                extension.setInheritOutput(true);
            }
        }

    }

    protected BuildResult doBuild(CompileScopeTestBuilder scope) {
        ProjectDescriptor descriptor = createProjectDescriptor(new BuildLoggingManager((myLogger)));
        try {
            myLogger.clearFilesData();
            myLogger.clearLog();
            BuildResult result = doBuild(descriptor, scope);
            myLastBuildResult = result;
            return result;
        } finally {
            descriptor.release();
        }
    }

    protected BuildResult doBuild(ProjectDescriptor descriptor, CompileScopeTestBuilder scopeTestBuilder) {
        IncProjectBuilder builder = new IncProjectBuilder(descriptor, BuilderRegistry.getInstance(), myBuildParams, CanceledStatus.NULL, true);
        BuildResult result = new BuildResult();
        builder.addMessageHandler(result);
        try {
            builder.build(scopeTestBuilder.build(), false);
            result.storeMappingsDump(descriptor);
        } catch (RebuildRequestedException | IOException  e) {
            throw new RuntimeException(e);
        }

        myLastBuildResult = result;
        return result;
    }

    protected void assertCompiled(String... paths) {
        try {
        myLogger.assertCompiled(Builder.BUILDER_NAME, new File[]{myProjectDir, myDataStorageRoot}, paths);
        } catch (AssertionError error) {
            String logText = myLogger.getLogText();
            String buildMessages = buildMessagesText();
            if (!logText.isBlank() || !buildMessages.isBlank()) {
                StringBuilder details = new StringBuilder();
                if (!logText.isBlank()) {
                    details.append("\nBuild log:\n").append(logText);
                }
                if (!buildMessages.isBlank()) {
                    details.append("\nBuild messages:\n").append(buildMessages);
                }
                throw new AssertionError(error.getMessage() + details, error);
            }
            throw error;
        }
    }

    @NotNull
    private String buildMessagesText() {
        BuildResult result = myLastBuildResult;
        if (result == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        appendMessages(builder, "Errors", result.getMessages(BuildMessage.Kind.ERROR));
        appendMessages(builder, "Warnings", result.getMessages(BuildMessage.Kind.WARNING));
        appendMessages(builder, "Info", result.getMessages(BuildMessage.Kind.INFO));
        return builder.toString();
    }

    private static void appendMessages(@NotNull StringBuilder builder,
                                       @NotNull String label,
                                       @NotNull java.util.List<BuildMessage> messages) {
        if (messages.isEmpty()) {
            return;
        }

        if (!builder.isEmpty()) {
            builder.append('\n');
        }

        builder.append(label).append(":\n").append(StringUtil.join(messages, "\n"));
    }

    protected void assertSuccessfulWithLogs(BuildResult result) {
        try {
            result.assertSuccessful();
        } catch (AssertionError error) {
            String logText = myLogger.getLogText();
            if (!logText.isBlank()) {
                throw new AssertionError(error.getMessage() + "\nBuild log:\n" + logText, error);
            }
            throw error;
        }
    }

    protected void checkMappingsAreSameAfterRebuild(BuildResult makeResult) {
        String makeDump = makeResult.getMappingsDump();
        BuildResult rebuildResult = doBuild(CompileScopeTestBuilder.rebuild().all());
        assertSuccessfulWithLogs(rebuildResult);
        String rebuildDump = rebuildResult.getMappingsDump();
        assertEquals(rebuildDump, makeDump);
    }

    public String createFile(String relativePath, String text) {
        try {
            File file = new File(getOrCreateProjectDir(), relativePath);
            FileUtil.writeToFile(file, text);
            return FileUtil.toSystemIndependentName(file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File getOrCreateProjectDir() {
        if (myProjectDir == null) {
            try {
                myProjectDir = doGetProjectDir();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return myProjectDir;
    }

    protected File doGetProjectDir() throws IOException {
        return FileUtil.createTempDirectory("prj", null);
    }

    public String getAbsolutePath(String pathRelativeToProjectRoot) {
        return FileUtil.toSystemIndependentName(new File(getOrCreateProjectDir(), pathRelativeToProjectRoot).getAbsolutePath());
    }
}
