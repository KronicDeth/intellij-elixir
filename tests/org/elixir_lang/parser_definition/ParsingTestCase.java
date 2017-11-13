package org.elixir_lang.parser_definition;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ParserDefinition;
import com.intellij.mock.MockLocalFileSystem;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.extensions.ExtensionsArea;
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
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.openapi.vfs.impl.VirtualFileManagerImpl;
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
import java.util.LinkedList;
import java.util.List;

import static org.elixir_lang.SdkType.pathIsValidSdkHome;
import static org.elixir_lang.test.ElixirVersion.elixirSdkRelease;

/**
 * Created by luke.imhoff on 8/7/14.
 */
@org.junit.Ignore("abstract")
public abstract class ParsingTestCase extends com.intellij.testFramework.ParsingTestCase {
    public ParsingTestCase() {
        this("ex", new ElixirParserDefinition());
    }

    protected ParsingTestCase(String extension, ParserDefinition parserDefinition) {
        super("", extension, parserDefinition);
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

    protected void assertWithLocalError() {
        final FileViewProvider fileViewProvider = myFile.getViewProvider();
        PsiFile root = fileViewProvider.getPsi(ElixirLanguage.INSTANCE);
        final List<PsiElement> errorElementList = new LinkedList<PsiElement>();

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

        assertTrue("No PsiErrorElements found in parsed file PSI", !errorElementList.isEmpty());
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

    protected void assertWithoutLocalError() {
        final FileViewProvider fileViewProvider = myFile.getViewProvider();
        PsiFile root = fileViewProvider.getPsi(ElixirLanguage.INSTANCE);
        final List<PsiElement> errorElementList = new LinkedList<PsiElement>();

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

        assertTrue("PsiErrorElements found in parsed file PSI", errorElementList.isEmpty());
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
        MessageBus messageBus = messageBus();
        registerProjectFileIndex(messageBus);
        ProjectRootManager projectRootManager = registerProjectRootManager();

        assertTrue(pathIsValidSdkHome(sdkHome));

        registerExtensionPoint(OrderRootType.EP_NAME, OrderRootType.class);
        registerExtension(OrderRootType.EP_NAME, new JavadocOrderRootType());

        getApplication().addComponent(
                VirtualFileManager.class,
                new VirtualFileManagerImpl(
                        new VirtualFileSystem[]{
                                new MockLocalFileSystem()
                        },
                        messageBus
                )
        );

        ProjectJdkTable projectJdkTable = new ProjectJdkTableImpl();
        registerApplicationService(ProjectJdkTable.class, projectJdkTable);

        registerExtensionPoint(
                com.intellij.openapi.projectRoots.SdkType.EP_NAME,
                com.intellij.openapi.projectRoots.SdkType.class
        );
        registerExtension(com.intellij.openapi.projectRoots.SdkType.EP_NAME, new Type());
        registerExtension(com.intellij.openapi.projectRoots.SdkType.EP_NAME, new org.elixir_lang.sdk.erlang.Type());

        Sdk sdk = Type.createMockSdk(sdkHome, elixirSdkRelease());
        projectJdkTable.addJdk(sdk);

        ExtensionsArea area = Extensions.getArea(myProject);
        registerExtensionPoint(area, ProjectExtension.EP_NAME, ProjectExtension.class);

        registerExtensionPoint(FilePropertyPusher.EP_NAME, FilePropertyPusher.class);
        myProject.addComponent(PushedFilePropertiesUpdater.class, new PushedFilePropertiesUpdaterImpl(myProject));

        projectRootManager.setProjectSdk(sdk);
    }

    @NotNull
    protected static String ebinDirectory() {
        String ebinDirectory = System.getenv("ELIXIR_EBIN_DIRECTORY");

        assertNotNull("ELIXIR_EBIN_DIRECTORY is not set", ebinDirectory);

        return ebinDirectory;
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
