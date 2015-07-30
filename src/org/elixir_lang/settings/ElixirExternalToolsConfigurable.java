package org.elixir_lang.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.TitledSeparator;
import org.elixir_lang.mix.settings.MixConfigurationForm;
import org.elixir_lang.mix.settings.MixSettings;
import org.elixir_lang.sdk.ElixirSdkForSmallIdes;
import org.elixir_lang.sdk.ElixirSdkType;
import org.elixir_lang.sdk.ElixirSystemUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by zyuyou on 2015/5/26.
 */
public class ElixirExternalToolsConfigurable implements SearchableConfigurable, Configurable.NoScroll{
  public static final String ELIXIR_RELATED_TOOLS = "Elixir External Tools";

  /* Form Values */
  private JPanel myPanel;
  private MixConfigurationForm myMixConfigurationForm;
  private TitledSeparator mySdkTitledSeparator;
  private JLabel mySdkPathLabel;
  private TextFieldWithBrowseButton mySdkPathSelector;

  /* Self Defined Values */
  private final Project myProject;
  private MixSettings myMixSettings;

  public ElixirExternalToolsConfigurable(@NotNull Project project){
    myProject = project;
    myMixSettings = MixSettings.getInstance(project);
    mySdkPathSelector.addBrowseFolderListener("Select Elixir SDK path", "", null,
        FileChooserDescriptorFactory.createSingleFolderDescriptor().withTitle("Elixir SDK root"));
    
    if(StringUtil.isEmpty(myMixSettings.getMixPath())){
      VirtualFile baseDir = project.getBaseDir();
      if(baseDir != null){
        VirtualFile mix = baseDir.findChild("mix");
        if(mix != null){
          String canonicalPath = mix.getCanonicalPath();
          if(canonicalPath != null){
            myMixSettings.setMixPath(canonicalPath);
          }
        }
      }
    }

    if(!ElixirSystemUtil.isSmallIde()){
      mySdkPathLabel.setVisible(false);
      mySdkPathSelector.setVisible(false);
      mySdkTitledSeparator.setVisible(false);
    }

    reset();
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    myMixConfigurationForm.createComponent();
    return myPanel;
  }

  @NotNull
  @Override
  public String getId() {
    return ELIXIR_RELATED_TOOLS;
  }

  @Nls
  @Override
  public String getDisplayName() {
    return ELIXIR_RELATED_TOOLS;
  }

  @Nullable
  @Override
  public String getHelpTopic() {
    return null;
  }

  @Nullable
  @Override
  public Runnable enableSearch(String option) {
    return null;
  }

  @Override
  public void apply() throws ConfigurationException {
    myMixSettings.setMixPath(myMixConfigurationForm.getPath());

    if(ElixirSystemUtil.isSmallIde()){
      ElixirSdkForSmallIdes.setUpOrUpdateSdk(myProject, mySdkPathSelector.getText());
    }
  }

  @Override
  public boolean isModified() {
    return !myMixSettings.getMixPath().equals(myMixConfigurationForm.getPath())
        || !StringUtil.notNullize(ElixirSdkType.getSdkPath(myProject)).equals(mySdkPathSelector.getText());

  }

  @Override
  public void disposeUIResources() {
  }

  @Override
  public void reset() {
    myMixConfigurationForm.setPath(myMixSettings.getMixPath());

    mySdkPathSelector.setText(StringUtil.notNullize(ElixirSdkType.getSdkPath(myProject)));
  }
}
