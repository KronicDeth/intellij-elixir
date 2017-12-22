package org.elixir_lang.configuration;

import com.intellij.compiler.options.CompilerConfigurable;
import com.intellij.openapi.project.Project;
import org.elixir_lang.utils.AncestorAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;

/**
 * Created by zyuyou on 15/7/6.
 */
public class ElixirCompilerOptionsConfigurable extends CompilerConfigurable {
  private JPanel myRootPanel;
  private JCheckBox myUseMixCompilerCheckBox;
  private JCheckBox myAttachDebugInfoCheckBox;
  private JCheckBox myIgnoreModuleConflictCheckBox;
  private JCheckBox myAttachDocsCheckBox;
  private JCheckBox myWarningsAsErrorsCheckBox;

  private final ElixirCompilerSettings mySettings;

  public ElixirCompilerOptionsConfigurable(Project project) {
    super(project);

    mySettings = ElixirCompilerSettings.getInstance(project);

    /*
    * for now, --warnings-as-errors not like erlang's warn_as_error, it just return non-zero exit code
    * @see https://github.com/elixir-lang/elixir/issues/3116#issuecomment-87316125
    * */
    myWarningsAsErrorsCheckBox.setVisible(false);

    setupUiListeners();
  }

  private void setupUiListeners(){
    myRootPanel.addAncestorListener(new AncestorAdapter(){
      @Override
      public void ancestorAdded(AncestorEvent event) {
        reset();
      }
    });
  }

  @NotNull
  @Override
  public String getId() {
    return "Elixir compiler";
  }

  @Override
  public String getDisplayName() {
    return "Elixir Compiler";
  }

  @Override
  public JComponent createComponent() {
    return myRootPanel;
  }

  @Override
  public void reset() {
    myUseMixCompilerCheckBox.setSelected(mySettings.isUseMixCompilerEnabled());
    myAttachDocsCheckBox.setSelected(mySettings.isAttachDocsEnabled());
    myAttachDebugInfoCheckBox.setSelected(mySettings.isAttachDebugInfoEnabled());
    myWarningsAsErrorsCheckBox.setSelected(mySettings.isWarningsAsErrorsEnabled());
    myIgnoreModuleConflictCheckBox.setSelected(mySettings.isIgnoreModuleConflictEnabled());
  }

  @Override
  public void apply() {
    mySettings.setUseMixCompilerEnabled(myUseMixCompilerCheckBox.isSelected());
    mySettings.setAttachDocsEnabled(myAttachDocsCheckBox.isSelected());
    mySettings.setAttachDebugInfoEnabled(myAttachDebugInfoCheckBox.isSelected());
    mySettings.setWarningsAsErrorsEnabled(myWarningsAsErrorsCheckBox.isSelected());
    mySettings.setIgnoreModuleConflictEnabled(myIgnoreModuleConflictCheckBox.isSelected());
  }

  @Override
  public boolean isModified() {
    return myUseMixCompilerCheckBox.isSelected() != mySettings.isUseMixCompilerEnabled() ||
        myAttachDocsCheckBox.isSelected() != mySettings.isAttachDocsEnabled() ||
        myAttachDebugInfoCheckBox.isSelected() != mySettings.isAttachDebugInfoEnabled() ||
        myWarningsAsErrorsCheckBox.isSelected() != mySettings.isWarningsAsErrorsEnabled() ||
        myIgnoreModuleConflictCheckBox.isSelected() != mySettings.isIgnoreModuleConflictEnabled();
  }
}
