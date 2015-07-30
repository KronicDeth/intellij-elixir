package org.elixir_lang.mix.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFileFactory;
import org.elixir_lang.ElixirFileType;
import org.elixir_lang.psi.ElixirFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zyuyou on 15/7/2.
 */
public class ElixirScriptFileUtil {
  @NotNull
  public static String readLine(InputStream stream) {
    BufferedReader errReader = new BufferedReader(new InputStreamReader(stream));
    try {
      return StringUtil.notNullize(errReader.readLine());
    } catch (IOException ignore) {
    } finally {
      try {
        errReader.close();
      } catch (IOException ignore) {
      }
    }
    return "";
  }

  @Nullable
  public static ElixirFile createPsi(@NotNull VirtualFile file){
    if(file.getFileType() != ElixirFileType.SCRIPT) return null;

    try{
      String text = StringUtil.convertLineSeparators(VfsUtilCore.loadText(file));
      Project defaultProject = ProjectManager.getInstance().getDefaultProject();
      return (ElixirFile) PsiFileFactory.getInstance(defaultProject).createFileFromText(file.getName(), file.getFileType(), text);
    } catch (IOException e) {
      return null;
    }
  }
}