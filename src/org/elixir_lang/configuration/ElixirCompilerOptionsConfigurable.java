package org.elixir_lang.configuration;

import com.intellij.compiler.options.CompilerConfigurable;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.ex.Settings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.util.ObjectUtils;
import io.netty.util.internal.ObjectUtil;
import org.elixir_lang.mix.settings.MixSettings;
import org.elixir_lang.settings.ElixirExternalToolsConfigurable;
import org.elixir_lang.utils.AncestorAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by zyuyou on 15/7/6.
 */
public class ElixirCompilerOptionsConfigurable extends CompilerConfigurable {
  private JPanel myRootPanel;
  private JCheckBox myUseMixCompilerCheckBox;
  private JCheckBox myAddDebugInfoCheckBox;
  private JButton myConfigureMixButton;

  private final ElixirCompilerSettings mySettings;
  private final Project myProject;

  public ElixirCompilerOptionsConfigurable(Project project) {
    super(project);

    myProject = project;
    mySettings = ElixirCompilerSettings.getInstance(project);
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

    myAddDebugInfoCheckBox.setSelected(mySettings.isAddDebugInfoEnabled());
  }

  @Override
  public void apply() throws ConfigurationException {
    mySettings.setUseMixCompilerEnabled(myUseMixCompilerCheckBox.isSelected());
    mySettings.setAddDebugInfoEnabled(myAddDebugInfoCheckBox.isSelected());
  }

  @Override
  public boolean isModified() {
    return myUseMixCompilerCheckBox.isSelected() != mySettings.isUseMixCompilerEnabled() ||
        myAddDebugInfoCheckBox.isSelected() != mySettings.isAddDebugInfoEnabled();
  }
}
