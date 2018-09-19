package org.elixir_lang.mix.project._import.step;

import com.intellij.ide.util.ElementsChooser;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.projectImport.SelectImportedProjectsStep;
import org.elixir_lang.Icons;
import org.elixir_lang.mix.project._import.OtpApp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zyuyou on 15/7/3.
 */
public class SelectOtpApps extends SelectImportedProjectsStep<OtpApp> {
  private final Set<String> myDuplicateModuleNames = new HashSet<String>();

  public SelectOtpApps(@NotNull WizardContext context) {
    super(context);
    fileChooser.addElementsMarkListener(new ElementsChooser.ElementsMarkListener<OtpApp>() {
      @Override
      public void elementMarkChanged(OtpApp element, boolean isMarked) {
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
  protected String getElementText(OtpApp otpApp) {
    return otpApp.toString();
  }

  @Nullable
  @Override
  protected Icon getElementIcon(OtpApp otpApp) {
    return myDuplicateModuleNames.contains(otpApp.getName()) ? Icons.MIX_MODULE_CONFLICT : null;
  }

  /**
   * for test
   * */
  public void autoResolveConflicts(){
    // NOTE: It is assumed that elements are sorted by names, therefore conflicting names a grouped together.
    String previousAppName = null;
    for(OtpApp selectedOtpApp : fileChooser.getMarkedElements()){
      if(selectedOtpApp.getName().equals(previousAppName)){
        fileChooser.setElementMarked(selectedOtpApp, false);
      }else{
        previousAppName = selectedOtpApp.getName();
      }
    }
  }

  private void evalDuplicates(){
    List<OtpApp> selectedOtpApps = fileChooser.getMarkedElements();
    Set<String> contains = new HashSet<String>(selectedOtpApps.size());
    myDuplicateModuleNames.clear();
    for (OtpApp otpApp :selectedOtpApps){
      if(!contains.add(otpApp.getName())){
        myDuplicateModuleNames.add(otpApp.getName());
      }
    }
  }

}
