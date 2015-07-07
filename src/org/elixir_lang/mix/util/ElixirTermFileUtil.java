package org.elixir_lang.mix.util;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zyuyou on 15/7/2.
 */
public class ElixirTermFileUtil {
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

}
