package org.elixir_lang.mix.importWizard;

import com.intellij.compiler.CompilerWorkspaceConfiguration;
import com.intellij.ide.projectWizard.ProjectWizardTestCase;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathMacros;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.impl.ModuleRootManagerImpl;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.Consumer;
import org.elixir_lang.configuration.ElixirCompilerSettings;
import org.elixir_lang.sdk.ElixirSdkRelease;
import org.elixir_lang.sdk.ElixirSdkType;
import org.jdom.Document;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Created by zyuyou on 15/7/17.
 */
public class MixProjectImportBuilderTest extends ProjectWizardTestCase{
  private static final String MODULE_DIR = "MODULE_DIR";
  private static final String TEST_DATA = "testData/org/elixir_lang/";
  private static final String TEST_DATA_IMPORT = TEST_DATA + "mix/import/";
  private static final String MOCK_SDK_DIR = TEST_DATA + "mockSdk-1.0.4/";

  public void testFromRootMixExsFile() throws Exception{ doTest(null); }

  @Override
  protected String getTestDirectoryName() {
    return super.getTestDirectoryName();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    createMockSdk();
    File currentTestRoot = new File(TEST_DATA_IMPORT, getTestName(true));
    FileUtil.copyDir(currentTestRoot, new File(getProject().getBaseDir().getPath()));
  }

  private static void createMockSdk(){
    final Sdk mockSdk = ElixirSdkType.createMockSdk(MOCK_SDK_DIR, ElixirSdkRelease.V_1_0_4);
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
        ProjectJdkTable.getInstance().addJdk(mockSdk);
      }
    });
  }

  private Project doTest(@Nullable Consumer<ModuleWizardStep> adjuster) throws Exception{
    String projectPath = getProject().getBaseDir().getPath();
    String importFromPath = projectPath + "/test/";
    Module firstModule = importProjectFrom(importFromPath, adjuster, new MixProjectImportProvider(new MixProjectImportBuilder()));
    Project createProject = firstModule.getProject();
    validateProject(createProject);
    for(Module importedModule: ModuleManager.getInstance(createProject).getModules()){
      validateModule(importedModule);
    }
    return createProject;
  }

  private static void validateProject(@NotNull Project project){
    ElixirCompilerSettings compilerSettings = ElixirCompilerSettings.getInstance(project);
    assertNotNull("Elixir compiler settings are not created.", compilerSettings);
    assertTrue("Mix compiler is not set as default compiler.", compilerSettings.isUseMixCompilerEnabled());
    assertFalse("Clear output directory flag was not unset", CompilerWorkspaceConfiguration.getInstance(project).CLEAR_OUTPUT_DIRECTORY);
  }

  private void validateModule(@NotNull Module module) throws Exception{
    String importedModulePath = getProject().getBaseDir().getPath();

    Element actualImlElement = new Element("root");
    ((ModuleRootManagerImpl) ModuleRootManager.getInstance(module)).getState().writeExternal(actualImlElement);
    PathMacros.getInstance().setMacro(MODULE_DIR, importedModulePath);
    PathMacroManager.getInstance(module).collapsePaths(actualImlElement);
    PathMacroManager.getInstance(getProject()).collapsePaths(actualImlElement);
    PathMacros.getInstance().removeMacro(MODULE_DIR);

    String projectPath = getProject().getBaseDir().getPath();
    File expectedImlFile = new File(projectPath + "/expected/" + module.getName() + ".iml");
    Document expectedIml = JDOMUtil.loadDocument(expectedImlFile);
    Element expectedImlElement = expectedIml.getRootElement();

    String errorMsg = "Configuration of module " + module.getName() + " does not meet expectations.\nExpected:\n" +
        new String(JDOMUtil.printDocument(expectedIml, "\n")) +
        "\nBut got:\n" + new String(JDOMUtil.printDocument(new Document(actualImlElement), "\n"));

    assertTrue(errorMsg, JDOMUtil.areElementsEqual(expectedImlElement, actualImlElement));
  }
}
