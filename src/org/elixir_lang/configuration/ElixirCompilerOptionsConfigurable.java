package org.elixir_lang.configuration;

import com.intellij.compiler.options.CompilerConfigurable;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.ex.Settings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ObjectUtils;
import org.elixir_lang.mix.settings.MixSettings;
import org.elixir_lang.settings.ElixirExternalToolsConfigurable;
import org.elixir_lang.utils.AncestorAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by zyuyou on 15/7/6.
 */
public class ElixirCompilerOptionsConfigurable extends CompilerConfigurable {
  private JPanel myRootPanel;
  private JCheckBox myUseMixCompilerCheckBox;
  private JCheckBox myAttachDebugInfoCheckBox;
  private JButton myConfigureMixButton;
  private JCheckBox myIgnoreModuleConflictCheckBox;
  private JCheckBox myAttachDocsCheckBox;
  private JCheckBox myWarningsAsErrorsCheckBox;

  private final ElixirCompilerSettings mySettings;
  private final Project myProject;

  public ElixirCompilerOptionsConfigurable(Project project) {
    super(project);

    myProject = project;
    mySettings = ElixirCompilerSettings.getInstance(project);

    /*
    * for now, --warnings-as-errors not like erlang's warn_as_error, it just return non-zero exit code
    * @see https://github.com/elixir-lang/elixir/issues/3116#issuecomment-87316125
    * */
    myWarningsAsErrorsCheckBox.setVisible(false);

    setupUiListeners();
  }

  private void setupUiListeners(){
    myConfigureMixButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DataContext context = DataManager.getInstance().getDataContext(myConfigureMixButton);
        Settings settings = ObjectUtils.assertNotNull(Settings.KEY.getData(context));
        Configurable configurable = settings.find(ElixirExternalToolsConfigurable.ELIXIR_RELATED_TOOLS);
        if(configurable != null){
          settings.select(configurable);
        }
      }
    });

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
    boolean mixPathIsSet = StringUtil.isNotEmpty(MixSettings.getInstance(myProject).getMixPath());
    myConfigureMixButton.setVisible(!mixPathIsSet);

    myUseMixCompilerCheckBox.setEnabled(mixPathIsSet);
    myUseMixCompilerCheckBox.setSelected(mixPathIsSet && mySettings.isUseMixCompilerEnabled());

    myAttachDocsCheckBox.setSelected(mySettings.isAttachDocsEnabled());
    myAttachDebugInfoCheckBox.setSelected(mySettings.isAttachDebugInfoEnabled());
    myWarningsAsErrorsCheckBox.setSelected(mySettings.isWarningsAsErrorsEnabled());
    myIgnoreModuleConflictCheckBox.setSelected(mySettings.isIgnoreModuleConflictEnabled());
  }

  @Override
  public void apply() throws ConfigurationException {
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
