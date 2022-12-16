package org.elixir_lang.jps;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.io.FileSystemUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.util.io.TestFileSystemBuilder;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.api.CanceledStatus;
import org.jetbrains.jps.builders.impl.BuildDataPathsImpl;
import org.jetbrains.jps.builders.impl.BuildRootIndexImpl;
import org.jetbrains.jps.builders.impl.BuildTargetIndexImpl;
import org.jetbrains.jps.builders.impl.BuildTargetRegistryImpl;
import org.jetbrains.jps.builders.logging.BuildLoggingManager;
import org.jetbrains.jps.builders.storage.BuildDataPaths;
import org.jetbrains.jps.cmdline.ClasspathBootstrap;
import org.jetbrains.jps.cmdline.ProjectDescriptor;
import org.jetbrains.jps.incremental.BuilderRegistry;
import org.jetbrains.jps.incremental.IncProjectBuilder;
import org.jetbrains.jps.incremental.RebuildRequestedException;
import org.jetbrains.jps.incremental.fs.BuildFSState;
import org.jetbrains.jps.incremental.relativizer.PathRelativizerService;
import org.jetbrains.jps.incremental.storage.BuildDataManager;
import org.jetbrains.jps.incremental.storage.BuildTargetsState;
import org.jetbrains.jps.incremental.storage.ProjectStamps;
import org.jetbrains.jps.indices.ModuleExcludeIndex;
import org.jetbrains.jps.indices.impl.IgnoredFileIndexImpl;
import org.jetbrains.jps.indices.impl.ModuleExcludeIndexImpl;
import org.jetbrains.jps.model.*;
import org.jetbrains.jps.model.java.*;
import org.jetbrains.jps.model.library.JpsOrderRootType;
import org.jetbrains.jps.model.library.JpsTypedLibrary;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.library.sdk.JpsSdkReference;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsModuleType;
import org.jetbrains.jps.model.module.JpsSdkReferencesTable;
import org.jetbrains.jps.model.serialization.JpsProjectLoader;
import org.jetbrains.jps.model.serialization.PathMacroUtil;
import org.jetbrains.jps.util.JpsPathUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zyuyou on 15/7/17.
 */
public abstract class JpsBuildTestCase extends UsefulTestCase {
    private File myProjectDir;
    private JpsSdk<JpsDummyElement> myJdk;
    private TestProjectBuilderLogger myLogger;

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
        myBuildParams = new HashMap<String, String>();
    }

    @Override
    protected void tearDown() throws Exception {
        myProjectDir = null;
        super.tearDown();
    }

    protected static void assertOutput(String outputPath, TestFileSystemBuilder expected) {
        expected.build().assertDirectoryEqual(new File(FileUtil.toSystemIndependentName(outputPath)));
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
            e.printStackTrace();
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

    private JpsSdk<JpsDummyElement> addJdk(@SuppressWarnings("SameParameterValue") String name) {
        try {
            return addJdk(name, FileUtil.toSystemIndependentName(ClasspathBootstrap.getResourceFile(Object.class).getCanonicalPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected JpsSdk addJdk(String name, String path) {
        String homePath = System.getProperty("java.home");
        String versionString = System.getProperty("java.version");
        JpsTypedLibrary<JpsSdk<JpsDummyElement>> jdk = myModel.getGlobal().addSdk(name, homePath, versionString, JpsJavaSdkType.INSTANCE);
        jdk.addRoot(JpsPathUtil.pathToUrl(path), JpsOrderRootType.COMPILED);
        return jdk.getProperties();
    }

    protected String getProjectName() {
        return StringUtil.decapitalize(StringUtil.trimStart(getName(), "test"));
    }

    protected ProjectDescriptor createProjectDescriptor(BuildLoggingManager buildLoggingManager) {
        try {
            BuildTargetRegistryImpl targetRegistry = new BuildTargetRegistryImpl(myModel);
            ModuleExcludeIndex index = new ModuleExcludeIndexImpl(myModel);
            IgnoredFileIndexImpl ignoredFileIndex = new IgnoredFileIndexImpl(myModel);
            BuildDataPaths dataPaths = new BuildDataPathsImpl(myDataStorageRoot);
            BuildRootIndexImpl buildRootIndex = new BuildRootIndexImpl(targetRegistry, myModel, index, dataPaths, ignoredFileIndex);
            BuildTargetIndexImpl targetIndex = new BuildTargetIndexImpl(targetRegistry, buildRootIndex);
            BuildTargetsState targetsState = new BuildTargetsState(dataPaths, myModel, buildRootIndex);
            PathRelativizerService relativizer = new PathRelativizerService(myModel.getProject());
            ProjectStamps timestamps = new ProjectStamps(myDataStorageRoot, targetsState, relativizer);
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

    protected void loadProject(String projectPath, Map<String, String> pathVariables) {

        try {
            String testDataRootPath = getTestDataRootPath();
            String fullProjectPath = FileUtil.toSystemDependentName(testDataRootPath != null ? testDataRootPath + "/" + projectPath : projectPath);
            Map<String, String> allPathVariables = new HashMap<String, String>(pathVariables.size() + 1);
            allPathVariables.putAll(pathVariables);
            allPathVariables.put(PathMacroUtil.APPLICATION_HOME_DIR, PathManager.getHomePath());
            JpsProjectLoader.loadProject(myProject, allPathVariables, Paths.get(fullProjectPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    protected String getTestDataRootPath() {
        return null;
    }

    protected <T extends JpsElement> JpsModule addModule(String moduleName,
                                                         String[] srcPaths,
                                                         @Nullable String outputPath,
                                                         @Nullable String testOutputPath,
                                                         JpsSdk<T> sdk) {

        return addModule(moduleName, srcPaths, outputPath, testOutputPath, sdk, JpsJavaModuleType.INSTANCE);
    }

    protected <T extends JpsElement, M extends JpsModuleType & JpsElementTypeWithDefaultProperties> JpsModule addModule(
            String moduleName, String[] srcPaths, @Nullable String outputPath, @Nullable String testOutputPath,
            JpsSdk<T> sdk, M moduleType) {

        JpsModule module = myProject.addModule(moduleName, moduleType);
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

        return module;
    }

    protected void rebuildAll() {
        doBuild(CompileScopeTestBuilder.rebuild().all()).assertSuccessful();
    }

    protected BuildResult makeAll() {
        return doBuild(CompileScopeTestBuilder.make().all());
    }

    protected BuildResult doBuild(CompileScopeTestBuilder scope) {
        ProjectDescriptor descriptor = createProjectDescriptor(new BuildLoggingManager((myLogger)));
        try {
            myLogger.clear();
            return doBuild(descriptor, scope);
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
        } catch (RebuildRequestedException e) {
            throw new RuntimeException(e);
        }

        return result;
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

    public JpsModule addModule(String moduleName, String... srcPaths) {
        if (myJdk == null) {
            myJdk = addJdk("1.8");
        }
        return addModule(moduleName, srcPaths, getAbsolutePath("out/production/" + moduleName), null, myJdk);
    }


}
