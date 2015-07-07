package org.elixir_lang.mix.importWizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectJdkForModuleStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportProvider;
import org.elixir_lang.sdk.ElixirSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by zyuyou on 15/7/1.
 */
public class MixProjectImportProvider extends ProjectImportProvider {
  protected MixProjectImportProvider(@NotNull MixProjectImportBuilder builder) {
    super(builder);
  }

  @Override
  public ModuleWizardStep[] createSteps(@NotNull WizardContext context){
    return new ModuleWizardStep[]{
        new MixProjectRootStep(context),
        new SelectImportedOtpAppsStep(context),
        new ProjectJdkForModuleStep(context, ElixirSdkType.getInstance())
    };
  }

  @Override
  protected boolean canImportFromFile(VirtualFile file) {
    // todo: import project from file(mix.exs)
    //    return "mix.exs".equals(file.getName());
    return false;
  }

  @Override
  public String getPathToBeImported(VirtualFile file) {
    return file.getPath();
  }

  @Nullable
  @Override
  public String getFileSample() {
    return "<b>Mix</b> ex-script file (mix.exs)";
  }
}
