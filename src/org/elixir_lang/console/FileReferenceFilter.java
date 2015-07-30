package org.elixir_lang.console;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.filters.InvalidExpressionException;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/console/FileReferenceFilter.java
 */
public final class FileReferenceFilter implements Filter {
  public static final String PATH_MACROS = "$FILE_PATH$";
  public static final String LINE_MACROS = "$LINE$";
  public static final String COLUMN_MACROS = "$COLUMN$";

  private static final String FILE_PATH_REGEXP = "((?:\\p{Alpha}\\:)?[0-9 a-z_A-Z\\-\\\\./]+)";
  private static final String NUMBER_REGEXP = "([0-9]+)";

  private static final Pattern PATTERN_FILENAME = Pattern.compile("[/\\\\]?([^/\\\\]*?\\.ex)$");

  private final Pattern myPattern;
  private final Project myProject;
  private final int myFileMatchGroup;
  private final int myLineMatchGroup;
  private final int myColumnMatchGroup;

  public FileReferenceFilter(@NotNull Project project, @NonNls @NotNull String expression){
    myProject = project;

    if(StringUtil.isEmpty(expression)){
      throw new InvalidExpressionException("expression is empty.");
    }

    int filePathIndex = expression.indexOf(PATH_MACROS);
    int lineIndex = expression.indexOf(LINE_MACROS);
    int columnIndex = expression.indexOf(COLUMN_MACROS);

    if(filePathIndex == -1){
      throw new InvalidExpressionException("Expression must contain " + PATH_MACROS + " marcos.");
    }
    TreeMap<Integer, String> map = new TreeMap<Integer, String>();
    map.put(filePathIndex, PATH_MACROS);
    expression = StringUtil.replace(expression, PATH_MACROS, FILE_PATH_REGEXP);

    if(lineIndex != -1){
      expression = StringUtil.replace(expression, LINE_MACROS, NUMBER_REGEXP);
      map.put(lineIndex, LINE_MACROS);
    }

    if(columnIndex != -1){
      expression = StringUtil.replace(expression, COLUMN_MACROS, NUMBER_REGEXP);
      map.put(columnIndex, COLUMN_MACROS);
    }

    // The block below determines the registers based on the sorted map.
    int count = 0;
    for (Integer integer : map.keySet()){
      count++;
      String s = map.get(integer);

      if(PATH_MACROS.equals(s)){
        filePathIndex = count;
      }else if(LINE_MACROS.equals(s)){
        lineIndex = count;
      }else if(COLUMN_MACROS.equals(s)){
        columnIndex = count;
      }
    }

    myFileMatchGroup = filePathIndex;
    myLineMatchGroup = lineIndex;
    myColumnMatchGroup = columnIndex;
    myPattern = Pattern.compile(expression, Pattern.MULTILINE);
  }

  @Nullable
  @Override
  public Result applyFilter(@NotNull String line, int entireLength) {
    Matcher matcher = myPattern.matcher(line);
    if(!matcher.find()){
      return null;
    }
    String filePath = matcher.group(myFileMatchGroup);
    int fileLine = matchGroupToNumber(matcher, myLineMatchGroup);
    int fileColumn = matchGroupToNumber(matcher, myColumnMatchGroup);

    int highlightStartOffset = entireLength - line.length() + matcher.start(0);
    int highlightEndOffset = highlightStartOffset + matcher.end(0) - matcher.start(0);

    VirtualFile absolutePath = resolveAbsolutePath(filePath);
    HyperlinkInfo hyperlinkInfo = absolutePath != null ? new OpenFileHyperlinkInfo(myProject, absolutePath, fileLine, fileColumn) : null;

    return new Result(highlightStartOffset, highlightEndOffset, hyperlinkInfo);
  }

  private static int matchGroupToNumber(@NotNull Matcher matcher, int matchGroup){
    int number = 0;
    if(matchGroup != -1){
      try{
        number = Integer.parseInt(matcher.group(matchGroup));
      }catch (NumberFormatException ignored){
      }
    }
    return number > 0 ? number - 1 : 0;
  }

  @Nullable
  private VirtualFile resolveAbsolutePath(@NotNull String path){
    VirtualFile asIsFile = pathToVirtualFile(path);
    if(asIsFile != null){
      return asIsFile;
    }

    String projectBasedPath = path.startsWith(myProject.getBasePath()) ? path : new File(myProject.getBasePath(), path).getAbsolutePath();
    VirtualFile projectBasedFile = pathToVirtualFile(projectBasedPath);
    if(projectBasedFile != null){
      return projectBasedFile;
    }

    Matcher filenameMatcher = PATTERN_FILENAME.matcher(path);
    if(filenameMatcher.find()){
      String filename = filenameMatcher.group(1);
      GlobalSearchScope projectScope = ProjectScope.getProjectScope(myProject);
      PsiFile[] projectFiles = FilenameIndex.getFilesByName(myProject, filename, projectScope);
      if(projectFiles.length > 0){
        return projectFiles[0].getVirtualFile();
      }

      GlobalSearchScope libraryScope = ProjectScope.getLibrariesScope(myProject);
      PsiFile[] libraryFiles = FilenameIndex.getFilesByName(myProject, filename, libraryScope);
      if(libraryFiles.length > 0){
        return libraryFiles[0].getVirtualFile();
      }
    }

    return null;
  }

  @Nullable
  private static VirtualFile pathToVirtualFile(@NotNull String path){
    String normalizedPath = path.replace(File.separatorChar, '/');
    return LocalFileSystem.getInstance().findFileByPath(normalizedPath);


  }
}
