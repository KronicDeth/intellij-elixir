package org.elixir_lang.mix.project._import;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectJdkForModuleStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportProvider;
import org.elixir_lang.mix.project._import.step.Root;
import org.elixir_lang.mix.project._import.step.SelectOtpApps;
import org.elixir_lang.sdk.elixir.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by zyuyou on 15/7/1.
 */
public class Provider extends ProjectImportProvider {
  protected Provider(@NotNull Builder builder) {
    super(builder);
  }

  @Override
  public ModuleWizardStep[] createSteps(@NotNull WizardContext context){
    return new ModuleWizardStep[]{
        new ProjectJdkForModuleStep(context, Type.getInstance()),
        new Root(context),
        new SelectOtpApps(context)
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
