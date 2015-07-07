package org.elixir_lang.module;

import com.intellij.compiler.CompilerWorkspaceConfiguration;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.sdk.ElixirSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;

/**
 * Created by zyuyou on 2015/5/26.
 *
 */
public class ElixirModuleBuilder extends JavaModuleBuilder implements ModuleBuilderListener {
  @Override
  public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
    addListener(this);
    super.setupRootModel(rootModel);
  }

  @Override
  public ModuleType getModuleType() {
    return ElixirModuleType.getInstance();
  }

  @Override
  public boolean isSuitableSdkType(SdkTypeId sdkType) {
    return sdkType == ElixirSdkType.getInstance();
  }

  @Override
  public Icon getNodeIcon() {
    return ElixirIcons.FILE;
  }

  @Override
  public void moduleCreated(@NotNull Module module) {
    CompilerWorkspaceConfiguration.getInstance(module.getProject()).CLEAR_OUTPUT_DIRECTORY = false;
  }

  @Nullable
  @Override
  public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
    return new SdkSettingsStep(settingsStep, this, new Condition<SdkTypeId>() {
      @Override
      public boolean value(SdkTypeId sdkTypeId) {
        return isSuitableSdkType(sdkTypeId);
      }
    }){
      @Override
      public void updateDataModel(){
        super.updateDataModel();
        final String path = getContentEntryPath();
        if(path != null){
          setSourcePaths(Collections.singletonList(Pair.create(path + "/lib", "")));
        }
      }
    };
  }
}
