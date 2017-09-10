package org.elixir_lang.mix.importWizard;

import com.intellij.compiler.CompilerWorkspaceConfiguration;
import com.intellij.ide.projectWizard.ProjectWizardTestCase;
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
import org.elixir_lang.configuration.ElixirCompilerSettings;
import org.elixir_lang.sdk.elixir.Release;
import org.elixir_lang.sdk.elixir.Type;
import org.jdom.Document;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

/**
 * Created by zyuyou on 15/7/17.
 */
public class MixProjectImportBuilderTest extends ProjectWizardTestCase{
  private static final String[] FORMATS = new String[]{"14", "2017.1"};
  private static final String MODULE_DIR = "MODULE_DIR";
  private static final String TEST_DATA = "testData/org/elixir_lang/";
  private static final String TEST_DATA_IMPORT = TEST_DATA + "mix/import/";
  private static final String MOCK_SDK_DIR = TEST_DATA + "mockSdk-1.0.4/";

  public void testFromRootMixExsFile() throws Exception{ doTest(); }

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
    final Sdk mockSdk = Type.createMockSdk(MOCK_SDK_DIR, Release.V_1_0_4);
    ApplicationManager.getApplication().runWriteAction(() -> ProjectJdkTable.getInstance().addJdk(mockSdk));
  }

  private void doTest() throws Exception{
    String projectPath = getProject().getBaseDir().getPath();
    String importFromPath = projectPath + "/test/";
    Module firstModule = importProjectFrom(importFromPath, null, new MixProjectImportProvider(new MixProjectImportBuilder()));
    Project createProject = firstModule.getProject();
    validateProject(createProject);
    for(Module importedModule: ModuleManager.getInstance(createProject).getModules()){
      validateModule(importedModule);
    }
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
    StringBuilder expected = new StringBuilder("Configuration of module ").append(module.getName()).append(" does not meet expectations\nExpected:\n");
    boolean formattedFound = false;

    for (String format : FORMATS) {
      File expectedImlFile = new File(projectPath + "/expected/" + format + "/" + module.getName() + ".iml");
      Document expectedIml = JDOMUtil.loadDocument(expectedImlFile);
      Element expectedImlElement = expectedIml.getRootElement();
      expected.append(Arrays.toString(JDOMUtil.printDocument(expectedIml, "\n"))).append('\n');

      if (JDOMUtil.areElementsEqual(expectedImlElement, actualImlElement)) {
        assertTrue(true);
        formattedFound = true;
      }
    }

    if (!formattedFound) {
      String errorMsg = expected
              .append("\nBut got:\n")
              .append(Arrays.toString(JDOMUtil.printDocument(new Document(actualImlElement), "\n")))
              .toString();
     assertTrue(errorMsg, false);
    }
  }
}
