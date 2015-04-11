package org.elixir_lang.mix;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;

public class ModuleBuilder extends com.intellij.ide.util.projectWizard.ModuleBuilder {
    @Override
    public com.intellij.openapi.module.ModuleType getModuleType() {
        return ModuleType.getInstance();
    }

    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        // TODO setup Sdk.  See com.jetbrains.python.module.PythonModuleBuilderBAse.setupRootModule
        doAddContentEntry(modifiableRootModel);
    }
}
