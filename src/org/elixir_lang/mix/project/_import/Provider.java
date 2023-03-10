package org.elixir_lang.mix.project._import;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportBuilder;
import com.intellij.projectImport.ProjectImportProvider;
import org.elixir_lang.mix.project._import.step.ElixirSdkForModuleStep;
import org.elixir_lang.mix.project._import.step.Root;
import org.elixir_lang.mix.project._import.step.SelectOtpApps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Provider extends ProjectImportProvider {
    @Override
    public ModuleWizardStep[] createSteps(@NotNull WizardContext context) {
        return new ModuleWizardStep[]{
                new ElixirSdkForModuleStep(context),
                new Root(context),
                new SelectOtpApps(context)
        };
    }

    @Override
    protected ProjectImportBuilder doGetBuilder() {
        return ProjectImportBuilder.EXTENSIONS_POINT_NAME.findExtensionOrFail(Builder.class);
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
