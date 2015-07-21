package org.elixir_lang.jps.builder;

import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zyuyou on 15/7/12.
 */
public class ElixirCompilerError {
  private static final Pattern COMPILER_MESSAGE_PATTERN = Pattern.compile("^(.+):(\\d+):(\\s*warning:)?\\s*(.+)$");

  private static final int UNKNOWN_LINE_NUMBER = -1;

  private final String errorMessage;
  private final String url;
  private final int line;
  private final CompilerMessageCategory category;

  private ElixirCompilerError(String errorMessage, String url, int line, CompilerMessageCategory category){
    this.errorMessage = errorMessage;
    this.url = url;
    this.line = line;
    this.category = category;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getUrl() {
    return url;
  }

  public int getLine() {
    return line;
  }

  public CompilerMessageCategory getCategory() {
    return category;
  }

  @Nullable
  public static ElixirCompilerError create(String rootPath, String elixircMessage){
    Matcher matcher = COMPILER_MESSAGE_PATTERN.matcher(StringUtil.trimTrailing(elixircMessage));
    if(!matcher.matches()) return null;

    String relativeFilePath = FileUtil.toSystemIndependentName(matcher.group(1));
    String line = matcher.group(2);
    String warning = matcher.group(3);
    String details = matcher.group(4);

    String path = StringUtil.isEmpty(rootPath) ? relativeFilePath : new File(FileUtil.toSystemIndependentName(rootPath), relativeFilePath).getPath();
    int lineNumber = StringUtil.parseInt(line, UNKNOWN_LINE_NUMBER);
    CompilerMessageCategory category = warning != null ? CompilerMessageCategory.WARNING : CompilerMessageCategory.ERROR;
    assert path != null;
    return new ElixirCompilerError(details, VfsUtilCore.pathToUrl(path), lineNumber, category);
  }
}
