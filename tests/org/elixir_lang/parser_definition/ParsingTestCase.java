package org.elixir_lang.parser_definition;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.mock.MockApplication;
import com.intellij.mock.MockLocalFileSystem;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.impl.EditorFactoryImpl;
import com.intellij.openapi.extensions.DefaultPluginDescriptor;
import com.intellij.openapi.extensions.ExtensionPoint;
import com.intellij.openapi.extensions.ExtensionsArea;
import com.intellij.openapi.extensions.impl.ExtensionsAreaImpl;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.fileTypes.MockFileTypeManager;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkTableImpl;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.*;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import com.intellij.openapi.vfs.encoding.EncodingManagerImpl;
import com.intellij.openapi.vfs.impl.CoreVirtualFilePointerManager;
import com.intellij.openapi.vfs.impl.VirtualFileManagerImpl;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import com.intellij.psi.*;
import com.intellij.util.messages.MessageBus;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.ElixirParserDefinition;
import org.elixir_lang.intellij_elixir.Quoter;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.sdk.elixir.Type;
import org.jetbrains.annotations.NotNull;
import org.picocontainer.MutablePicoContainer;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.intellij.concurrency.IdeaForkJoinWorkerThreadFactory.setupForkJoinCommonPool;
import static org.elixir_lang.test.ElixirVersion.elixirSdkRelease;

/**
 * Created by luke.imhoff on 8/7/14.
 */
@org.junit.Ignore("abstract")
public abstract class ParsingTestCase extends com.intellij.testFramework.ParsingTestCase {
    public ParsingTestCase() {
        this("ex", new ElixirParserDefinition());
    }

    protected ParsingTestCase(String extension, ParserDefinition... parserDefinitions) {
        super("", extension, parserDefinitions);
    }

    @NotNull
    protected static MessageBus messageBus(@NotNull MutablePicoContainer appContainer) {
        return (MessageBus) appContainer.getComponentInstanceOfType(MessageBus.class);
    }

    protected void assertParsedAndQuotedAroundError() {
        assertParsedAndQuotedAroundError(true);
    }

    protected void assertParsedAndQuotedAroundError(boolean checkResult) {
        doTest(checkResult);
        assertQuotedAroundError();
    }

    protected void assertParsedAndQuotedAroundExit() {
        doTest(true);
        assertQuotedAroundExit();
    }

    protected void assertParsedAndQuotedCorrectly() {
        assertParsedAndQuotedCorrectly(true);
    }

    protected void assertParsedAndQuotedCorrectly(boolean checkResult) {
        doTest(checkResult);
        assertWithoutLocalError();
        assertQuotedCorrectly();
    }

    protected void assertParsedWithLocalErrorAndRemoteExit() {
        doTest(true);

        assertWithLocalError();
        Quoter.assertExit(myFile);
    }

    protected void assertParsedWithErrors() {
        assertParsedWithErrors(true);
    }

    protected void assertParsedWithErrors(boolean checkResult) {
        doTest(checkResult);

        assertWithLocalError();
        Quoter.assertError(myFile);
    }

    private List<PsiElement> localErrors(@NotNull Language language) {
        final FileViewProvider fileViewProvider = myFile.getViewProvider();
        PsiFile root = fileViewProvider.getPsi(language);
        final List<PsiElement> errorElementList = new LinkedList<>();

        root.accept(
                new PsiRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        if (element instanceof PsiErrorElement) {
                            errorElementList.add(element);
                        }

                        super.visitElement(element);
                    }
                }
        );

        return errorElementList;
    }

    protected void assertWithLocalError() {
        assertWithLocalError(ElixirLanguage.INSTANCE);
    }

    protected void assertWithLocalError(@NotNull Language language) {
        List<PsiElement> errorElementList = localErrors(language);

        assertTrue("No PsiErrorElements found in parsed file PSI", !errorElementList.isEmpty());
    }

    protected void assertWithoutLocalError() {
        assertWithoutLocalError(ElixirLanguage.INSTANCE);
    }

    protected void assertWithoutLocalError(@NotNull Language language) {
        List<PsiElement> errorElementList = localErrors(language);

        assertTrue("PsiErrorElements found in parsed file PSI", errorElementList.isEmpty());
    }

    protected void assertQuotedAroundError() {
        assertInstanceOf(ElixirPsiImplUtil.quote(myFile), OtpErlangObject.class);
        Quoter.assertError(myFile);
    }

    protected void assertQuotedAroundExit() {
        assertInstanceOf(ElixirPsiImplUtil.quote(myFile), OtpErlangObject.class);
        Quoter.assertExit(myFile);
    }

    protected void assertQuotedCorrectly() {
        Quoter.assertQuotedCorrectly(myFile);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setProjectSdkFromEbinDirectory();
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/parser_definition";
    }

    /**
     * Whether test is running on travis-ci.
     *
     * @return {@code true} if on Travis CI; {@code false} otherwise
     */
    protected boolean isTravis() {
        String travis = System.getenv("TRAVIS");

        return travis != null && travis.equals("true");
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }

    @NotNull
    protected MessageBus messageBus() {
        final MutablePicoContainer appContainer = getApplication().getPicoContainer();

        return ParsingTestCase.messageBus(appContainer);
    }

    @NotNull
    private DirectoryIndex registerDirectoryIndex(MessageBus messageBus)
            throws ClassNotFoundException, InvocationTargetException, InstantiationException, NoSuchMethodException,
            IllegalAccessException {
        /* MUST be registered before DirectoryIndex because DirectoryIndexImpl.markContentRootsForRefresh calls
            ModuleManager.getInstance(this.myProject).getModules();  */
        registerModuleManager(messageBus);

        DirectoryIndex directoryIndex = new DirectoryIndexImpl(myProject);
        myProject.registerService(DirectoryIndex.class, directoryIndex);

        return directoryIndex;
    }

    protected void registerProjectFileIndex()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {
        registerProjectFileIndex(messageBus());
    }

    private void registerProjectFileIndex(MessageBus messageBus)
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        DirectoryIndex directoryIndex = registerDirectoryIndex(messageBus);
        FileTypeRegistry fileTypeRegistry = new MockFileTypeManager();

        myProject.registerService(
                ProjectFileIndex.class,
                new ProjectFileIndexImpl(myProject, directoryIndex, fileTypeRegistry)
        );
    }

    private void registerModuleManager(MessageBus messageBus)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException,
            NoSuchMethodException {
        Class<?> moduleManagerComponentClass = Class.forName("com.intellij.openapi.module.impl.ModuleManagerComponent");
        Constructor<?> moduleManagerComponentConstructor;
        ModuleManager moduleManager = null;

        try {
            // IntelliJ > 2016.3
            moduleManagerComponentConstructor = moduleManagerComponentClass.getConstructor(
                    Project.class
            );
            moduleManager = (ModuleManager) moduleManagerComponentConstructor.newInstance(myProject);
        } catch (NoSuchMethodException e1) {
            try {
                // IntelliJ 2016.3
                moduleManagerComponentConstructor = moduleManagerComponentClass.getConstructor(
                        Project.class,
                        MessageBus.class
                );
                moduleManager = (ModuleManager) moduleManagerComponentConstructor.newInstance(myProject, messageBus);
            } catch (NoSuchMethodException e2) {
                moduleManagerComponentConstructor = moduleManagerComponentClass.getConstructor(
                        Project.class,
                        ProgressManager.class,
                        MessageBus.class
                );
                moduleManager = (ModuleManager) moduleManagerComponentConstructor.newInstance(
                        myProject,
                        new ProgressManagerImpl(),
                        messageBus
                );
            }
        }

        myProject.registerService(ModuleManager.class, moduleManager);
    }

    @NotNull
    protected ProjectRootManager registerProjectRootManager() {
        ProjectRootManager projectRootManager = new ProjectRootManagerImpl(myProject);
        myProject.registerService(ProjectRootManager.class, projectRootManager);

        return projectRootManager;
    }

    @NotNull
    protected Type registerElixirSdkType() {
        registerExtensionPoint(
                com.intellij.openapi.projectRoots.SdkType.EP_NAME,
                com.intellij.openapi.projectRoots.SdkType.class
        );
        registerExtension(com.intellij.openapi.projectRoots.SdkType.EP_NAME, new Type());
        Type elixirSdkType = Type.getInstance();

        assertNotNull(elixirSdkType);

        return elixirSdkType;
    }

    protected void setProjectSdkFromEbinDirectory()
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        setProjectSdkFromSdkHome(sdkHomeFromEbinDirectory());
    }

    private void setProjectSdkFromSdkHome(@NotNull String sdkHome)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {
        ProjectRootManager projectRootManager = registerProjectRootManager();
        MessageBus messageBus = messageBus();
        registerProjectFileIndex(messageBus);

        assertTrue(pathIsValidSdkHome(sdkHome));

        registerExtensionPoint(OrderRootType.EP_NAME, OrderRootType.class);
        registerExtension(OrderRootType.EP_NAME, new JavadocOrderRootType());
        MockApplication application = getApplication();

        ExtensionsAreaImpl applicationExtensionArea = application.getExtensionArea();

        applicationExtensionArea.registerExtensionPoint("com.intellij.virtualFileManagerListener", "VirtualFileManagerListener", ExtensionPoint.Kind.INTERFACE);
        application.registerService(
                VirtualFileManager.class,
                new VirtualFileManagerImpl(
                        Collections.singletonList(
                                new MockLocalFileSystem()
                        ),
                        messageBus
                )
        );
        application.registerService(VirtualFilePointerManager.class, new CoreVirtualFilePointerManager());

        registerExtensionPoint(
                com.intellij.openapi.projectRoots.SdkType.EP_NAME,
                com.intellij.openapi.projectRoots.SdkType.class
        );
        ProjectJdkTable projectJdkTable = new ProjectJdkTableImpl();
        application.registerService(ProjectJdkTable.class, projectJdkTable);

        registerExtension(com.intellij.openapi.projectRoots.SdkType.EP_NAME, new Type());
        registerExtension(com.intellij.openapi.projectRoots.SdkType.EP_NAME, new org.elixir_lang.sdk.erlang.Type());

        setupForkJoinCommonPool(true);

        EditorFactory editorFactory = new EditorFactoryImpl();
        application.registerService(EditorFactory.class, editorFactory);

        EncodingManager encodingManager = new EncodingManagerImpl();
        application.registerService(EncodingManager.class, encodingManager);

        Sdk sdk = Type.createMockSdk(sdkHome, elixirSdkRelease());
        projectJdkTable.addJdk(sdk);

        ExtensionsArea area = myProject.getExtensionArea();
        //noinspection UnstableApiUsage
        registerExtensionPoint((ExtensionsAreaImpl) area, ProjectExtension.EP_NAME, ProjectExtension.class);

        //noinspection UnstableApiUsage
        applicationExtensionArea.registerPoint(FilePropertyPusher.EP_NAME.getName(), FilePropertyPusher.class, new DefaultPluginDescriptor(getClass().getName() + "." + getName()));

        myProject.addComponent(PushedFilePropertiesUpdater.class, new PushedFilePropertiesUpdaterImpl(myProject));

        projectRootManager.setProjectSdk(sdk);
    }

    @NotNull
    protected static String ebinDirectory() {
        String ebinDirectory = System.getenv("ELIXIR_EBIN_DIRECTORY");

        assertNotNull("ELIXIR_EBIN_DIRECTORY is not set", ebinDirectory);

        return ebinDirectory;
    }

    public static boolean pathIsValidSdkHome(String path) {
        File bin = new File(path, "bin").getAbsoluteFile();
        File elixir = new File(bin, "elixir");
        File elixirc = new File(bin, "elixirc");
        File iex = new File(bin, "iex");
        File mix = new File(bin, "mix");

        return elixir.canExecute() && elixirc.canExecute() && iex.canExecute() && mix.canExecute();
    }

    @NotNull
    protected static String sdkHomeFromEbinDirectory() {
      return sdkHomeFromEbinDirectory(ebinDirectory()) ;
    }

    @NotNull
    protected static String sdkHomeFromEbinDirectory(@NotNull String ebinDirectory) {
        return new File(ebinDirectory)
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .toString();
    }
}
