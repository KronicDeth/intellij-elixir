package org.elixir_lang.mix.settings;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.labels.ActionLink;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.download.DownloadableFileDescription;
import com.intellij.util.download.DownloadableFileService;
import com.intellij.util.download.FileDownloader;
import org.elixir_lang.sdk.ElixirSdkRelease;
import org.elixir_lang.sdk.ElixirSystemUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.io.File;
import java.util.List;

import static org.elixir_lang.sdk.ElixirSystemUtil.transformStdoutLine;

/**
 * Created by zyuyou on 2015/5/26.
 */
public class MixConfigurationForm {
  /*
   * CONSTANTS
   */

  private static final Logger LOGGER = Logger.getInstance(MixConfigurationForm.class);
  private static final String[][] MIX_ARGUMENTS_ARRAY = new String[][]{
          {"--version"},
          // Elixir X.Y.Z for mix.bat before 1.2.  See https://github.com/elixir-lang/elixir/issues/4075
          {"--", "--version"}
  };
  private static final Function<String, String> STDOUT_LINE_TRANSFORMER = new Function<String, String>() {
    @Override
    public String fun(String line) {
      // Elixir X.Y.Z for mix.bat before 1.2
      // Mix X.Y.Z for all others
      if (line.startsWith("Mix")) {
        return line;
      }

      return null;
    }
  };

  /*
   * Fields
   */

  private JPanel myPanel;
  private JTextField myMixVersionText;
  private JPanel myLinkContainer;
  private TextFieldWithBrowseButton myMixPathSelector;
  private JCheckBox supportsFormatterOptionCheckBox;

  private boolean myMixPathValid;

  public MixConfigurationForm(){
    myMixPathSelector.addBrowseFolderListener("Select Mix executable", "", null,
        FileChooserDescriptorFactory.createSingleLocalFileDescriptor());
    myMixPathSelector.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(DocumentEvent documentEvent) {
        myMixPathValid = validateMixPath();
      }
    });
    myMixPathValid = false;
  }

  public void setPath(@NotNull String mixPath){
    if(!myMixPathSelector.getText().equals(mixPath)){
      myMixPathSelector.setText(mixPath);
      myMixPathValid = validateMixPath();
    }
  }

  @NotNull
  public String getPath(){
    return myMixPathSelector.getText();
  }

  public boolean getSupportsFormatterOption() { return supportsFormatterOptionCheckBox.isSelected(); }

  public boolean isPathValid(){
    return myMixPathValid;
  }

  @Nullable
  public JComponent createComponent(){
    return myPanel;
  }

  private boolean validateMixPath(){
    String mixPath = myMixPathSelector.getText();
    File mix = new File(mixPath);

    if (!mix.exists()) {
      return false;
    }

    if (!mix.canExecute()) {
      String reason = mix.getPath() + "is not executable.";
      LOGGER.warn("Can't detect Mix version: " + reason);
      return false;
    }

    File exeFile = mix.getAbsoluteFile();
    String exePath = exeFile.getPath();
    String workDir = exeFile.getParent();
    ProcessOutput output = null;
    boolean valid = false;

    for (String[] arguments : MIX_ARGUMENTS_ARRAY) {
      try {
        output = ElixirSystemUtil.getProcessOutput(3000, workDir, exePath, arguments);
      } catch (ExecutionException executionException) {
        LOGGER.warn(executionException);
      }

      if (output != null) {
        String transformedStdout = transformStdoutLine(output, STDOUT_LINE_TRANSFORMER);

        if (transformedStdout != null) {
          myMixVersionText.setText(transformedStdout);
          String versionString = transformedStdout.replaceAll("^[^0-9]*", "");

          // Support for the --formatter option may be added in a 1.3.x release, but I'm being conservative for now
          // and assuming it won't be released until 1.4
          ElixirSdkRelease elixirSdkRelease = ElixirSdkRelease.fromString(versionString);

          if (elixirSdkRelease != null) {
            supportsFormatterOptionCheckBox.setSelected(elixirSdkRelease.compareTo(ElixirSdkRelease.V_1_4) >= 0);
          }

          valid = true;

          break;
        } else {
          String stderr = output.getStderr();
          StringBuilder text = new StringBuilder("N/A");

          if (StringUtil.isNotEmpty(stderr)) {
            text.append(": Error: ").append(stderr);
          }

          myMixVersionText.setText(text.toString());
        }
      }
    }

    return valid;
  }

  private void createUIComponents(){
    myLinkContainer = new JPanel(new BorderLayout());
    ActionLink link = new ActionLink("Download the latest Mix version", new AnAction() {
      @Override
      public void actionPerformed(AnActionEvent anActionEvent) {
        DownloadableFileService service = DownloadableFileService.getInstance();
        DownloadableFileDescription mix = service.createFileDescription("http://s3.hex.pm/builds/mix/mix", "mix");
        FileDownloader downloader = service.createDownloader(ContainerUtil.list(mix), "mix");
        List<Pair<VirtualFile, DownloadableFileDescription>> pairs = downloader.downloadWithProgress(null, getEventProject(anActionEvent), myLinkContainer);

        if(pairs != null){
          for (Pair<VirtualFile, DownloadableFileDescription> pair : pairs){
            try {
              String path = pair.first.getCanonicalPath();
              if(path != null){
                FileUtilRt.setExecutableAttribute(path, true);
                myMixPathSelector.setText(path);
                validateMixPath();
              }
            }catch (Exception e){ // Ignore
            }
          }
        }
      }
    });

    myLinkContainer.add(link, BorderLayout.NORTH);
  }
}
