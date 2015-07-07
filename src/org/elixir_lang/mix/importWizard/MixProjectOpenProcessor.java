package org.elixir_lang.mix.importWizard;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectOpenProcessorBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by zyuyou on 15/7/1.
 */
public class MixProjectOpenProcessor extends ProjectOpenProcessorBase<MixProjectImportBuilder>{
  protected MixProjectOpenProcessor(@NotNull MixProjectImportBuilder builder) {
    super(builder);
  }

  @Nullable
  @Override
  public String[] getSupportedExtensions() {
    return new String[]{"mix.exs"};
  }

  @Override
  public boolean doQuickImport(@NotNull VirtualFile exsFile, @NotNull WizardContext wizardContext){
    VirtualFile projectRoot = exsFile.getParent();
    wizardContext.setProjectName(projectRoot.getName());
    getBuilder().setProjectRoot(projectRoot);
    return true;
  }

  @NotNull
  @Override
  public MixProjectImportBuilder getBuilder() {
    return super.getBuilder();
  }
}
