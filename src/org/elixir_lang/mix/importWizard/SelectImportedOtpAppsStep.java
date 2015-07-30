package org.elixir_lang.mix.importWizard;

import com.intellij.ide.util.ElementsChooser;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.projectImport.SelectImportedProjectsStep;
import org.elixir_lang.icons.ElixirIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zyuyou on 15/7/3.
 */
final class SelectImportedOtpAppsStep extends SelectImportedProjectsStep<ImportedOtpApp> {
  private final Set<String> myDuplicateModuleNames = new HashSet<String>();

  public SelectImportedOtpAppsStep(@NotNull WizardContext context) {
    super(context);
    fileChooser.addElementsMarkListener(new ElementsChooser.ElementsMarkListener<ImportedOtpApp>() {
      @Override
      public void elementMarkChanged(ImportedOtpApp element, boolean isMarked) {
        evalDuplicates();
        fileChooser.repaint();
      }
    });
  }

  @Override
  public void updateStep() {
    super.updateStep();
    evalDuplicates();
  }

  @Override
  public boolean validate() throws ConfigurationException {
    return super.validate() && myDuplicateModuleNames.isEmpty();
  }

  @Override
  protected String getElementText(ImportedOtpApp importedOtpApp) {
    return importedOtpApp.toString();
  }

  @Nullable
  @Override
  protected Icon getElementIcon(ImportedOtpApp importedOtpApp) {
    return myDuplicateModuleNames.contains(importedOtpApp.getName()) ? ElixirIcons.MIX_MODULE_CONFLICT : null;
  }

  /**
   * for test
   * */
  public void autoResolveConflicts(){
    // NOTE: It is assumed that elements are sorted by names, therefore conflicting names a grouped together.
    String previousAppName = null;
    for(ImportedOtpApp selectedOtpApp : fileChooser.getMarkedElements()){
      if(selectedOtpApp.getName().equals(previousAppName)){
        fileChooser.setElementMarked(selectedOtpApp, false);
      }else{
        previousAppName = selectedOtpApp.getName();
      }
    }
  }

  private void evalDuplicates(){
    List<ImportedOtpApp> selectedOtpApps = fileChooser.getMarkedElements();
    Set<String> contains = new HashSet<String>(selectedOtpApps.size());
    myDuplicateModuleNames.clear();
    for (ImportedOtpApp importedOtpApp:selectedOtpApps){
      if(!contains.add(importedOtpApp.getName())){
        myDuplicateModuleNames.add(importedOtpApp.getName());
      }
    }
  }

}
