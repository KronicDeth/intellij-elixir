package org.elixir_lang.mix.settings;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.labels.ActionLink;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.download.DownloadableFileDescription;
import com.intellij.util.download.DownloadableFileService;
import com.intellij.util.download.FileDownloader;
import org.elixir_lang.utils.ExtProcessUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Created by zyuyou on 2015/5/26.
 */
public class MixConfigurationForm {
  private JPanel myPanel;
  private JTextField myMixVersionText;
  private JPanel myLinkContainer;
  private TextFieldWithBrowseButton myMixPathSelector;

  private boolean myMixPathValid;

  public MixConfigurationForm(){
    myMixPathSelector.addBrowseFolderListener("Select Mix executable", "", null,
        FileChooserDescriptorFactory.createSingleFileDescriptor());
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

  public boolean isPathValid(){
    return myMixPathValid;
  }

  @Nullable
  public JComponent createComponent(){
    return myPanel;
  }

  private boolean validateMixPath(){
    String mixPath = myMixPathSelector.getText();
    if(!new File(mixPath).exists()) return false;

    ExtProcessUtil.ExtProcessOutput output = ExtProcessUtil.execAndGetFirstLine(3000, mixPath, "--version");
    String version = output.getStdOut();

    // Elixir X.Y.Z for mix.bat before 1.2
    // Mix X.Y.Z for all others
    if (version.startsWith("Mix")) {
      myMixVersionText.setText(version);
      return true;
    }

    // Elixir X.Y.Z for mix.bat before 1.2.  See https://github.com/elixir-lang/elixir/issues/4075
    output = ExtProcessUtil.execAndGetFirstLine(3000, mixPath, "--", "--version");
    version = output.getStdOut();

    // Elixir X.Y.Z for mix.bat before 1.2
    // Mix X.Y.Z for all others
    if (version.startsWith("Mix")) {
      myMixVersionText.setText(version);
      return true;
    }

    String stdErr = output.getStdErr();
    myMixVersionText.setText("N/A" + (StringUtil.isNotEmpty(stdErr) ? ": Error: " + stdErr : ""));

    return false;
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