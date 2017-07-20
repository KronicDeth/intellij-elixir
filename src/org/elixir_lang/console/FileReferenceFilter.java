package org.elixir_lang.console;

import com.intellij.execution.filters.Filter;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/console/FileReferenceFilter.java
 */
public final class FileReferenceFilter implements Filter {
    static final String LINE_MACROS = "$LINE$";
    static final String PATH_MACROS = "$FILE_PATH$";
    private static final String COLUMN_MACROS = "$COLUMN$";
    private static final String FILE_PATH_REGEXP = "\\s*([0-9 a-z_A-Z\\-\\\\./]+)";
    private static final String NUMBER_REGEXP = "([0-9]+)";

    private static final Pattern PATTERN_FILENAME = Pattern.compile("[/\\\\]?([^/\\\\]*?\\.exs?)$");
    private final int myColumnMatchGroup;
    private final int myFileMatchGroup;
    private final int myLineMatchGroup;
    private final Pattern myPattern;
    private final Project myProject;

    FileReferenceFilter(@NotNull Project project, @NonNls @NotNull String expression) {
        myProject = project;

        if (StringUtil.isEmpty(expression)) {
            throw new InvalidExpressionException("expression is empty.");
        }

        int filePathIndex = expression.indexOf(PATH_MACROS);
        int lineIndex = expression.indexOf(LINE_MACROS);
        int columnIndex = expression.indexOf(COLUMN_MACROS);

        if (filePathIndex == -1) {
            throw new InvalidExpressionException("Expression must contain " + PATH_MACROS + " marcos.");
        }
        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(filePathIndex, PATH_MACROS);
        expression = StringUtil.replace(expression, PATH_MACROS, FILE_PATH_REGEXP);

        if (lineIndex != -1) {
            expression = StringUtil.replace(expression, LINE_MACROS, NUMBER_REGEXP);
            map.put(lineIndex, LINE_MACROS);
        }

        if (columnIndex != -1) {
            expression = StringUtil.replace(expression, COLUMN_MACROS, NUMBER_REGEXP);
            map.put(columnIndex, COLUMN_MACROS);
        }

        // The block below determines the registers based on the sorted map.
        int count = 0;
        for (Integer integer : map.keySet()) {
            count++;
            String s = map.get(integer);

            if (PATH_MACROS.equals(s)) {
                filePathIndex = count;
            } else if (LINE_MACROS.equals(s)) {
                lineIndex = count;
            } else if (COLUMN_MACROS.equals(s)) {
                columnIndex = count;
            }
        }

        myFileMatchGroup = filePathIndex;
        myLineMatchGroup = lineIndex;
        myColumnMatchGroup = columnIndex;
        myPattern = Pattern.compile(expression, Pattern.MULTILINE);
    }

    private static int matchGroupToNumber(@NotNull Matcher matcher, int matchGroup) {
        int number = 0;

        if (matchGroup != -1) {
            try {
                number = Integer.parseInt(matcher.group(matchGroup));
            } catch (NumberFormatException ignored) {
            }
        }

        return number > 0 ? number - 1 : 0;
    }

    @Nullable
    private static VirtualFile pathToVirtualFile(@NotNull String path) {
        String normalizedPath = path.replace(File.separatorChar, '/');

        return LocalFileSystem.getInstance().findFileByPath(normalizedPath);
    }

    @Nullable
    @Override
    public Result applyFilter(@NotNull String line, int entireLength) {
        Matcher matcher = myPattern.matcher(line);
        Result result = null;

        if (matcher.find()) {
            String filePath = matcher.group(myFileMatchGroup);
            Collection<VirtualFile> virtualFileCollection = resolveVirtualFileCollection(filePath);

            if (virtualFileCollection.size() > 0) {
                List<ResultItem> resultItemList = new ArrayList<>(virtualFileCollection.size());
                int highlightStartOffset = entireLength - line.length() + matcher.start(1);
                int highlightEndOffset = highlightStartOffset + matcher.end(matcher.groupCount()) - matcher.start(1);
                int fileLine = matchGroupToNumber(matcher, myLineMatchGroup);
                int fileColumn = matchGroupToNumber(matcher, myColumnMatchGroup);

                for (VirtualFile virtualFile : virtualFileCollection) {
                    resultItemList.add(
                            new ResultItem(
                                    highlightStartOffset,
                                    highlightEndOffset,
                                    new OpenFileHyperlinkInfo(myProject, virtualFile, fileLine, fileColumn)
                            )
                    );
                }

                result = new Result(resultItemList);
            }
        }

        return result;
    }

    @NotNull
    private Collection<VirtualFile> resolveVirtualFileCollection(@NotNull String path) {
        VirtualFile asIsFile = pathToVirtualFile(path);

        if (asIsFile != null) {
            return Collections.singleton(asIsFile);
        }

        String basePath = myProject.getBasePath();
        VirtualFile projectBasedFile = null;

        if (basePath != null) {
            String projectBasedPath;

            if (path.startsWith(basePath)) {
                projectBasedPath = path;
            } else {
                projectBasedPath = new File(basePath, path).getAbsolutePath();
            }

            projectBasedFile = pathToVirtualFile(projectBasedPath);
        }

        Collection<VirtualFile> virtualFileCollection = null;

        if (projectBasedFile != null) {
            virtualFileCollection = Collections.singleton(projectBasedFile);
        } else {
            Matcher filenameMatcher = PATTERN_FILENAME.matcher(path);

            if (filenameMatcher.find()) {
                String filename = filenameMatcher.group(1);
                GlobalSearchScope projectScope = ProjectScope.getProjectScope(myProject);
                virtualFileCollection = resolveVirtualFileCollection(path, filename, projectScope);

                if (virtualFileCollection.size() < 1) {
                    GlobalSearchScope libraryScope = ProjectScope.getLibrariesScope(myProject);

                    virtualFileCollection = resolveVirtualFileCollection(path, filename, libraryScope);
                }
            }
        }

        if (virtualFileCollection == null) {
            virtualFileCollection = Collections.emptySet();
        }

        return virtualFileCollection;
    }

    @NotNull
    private Collection<VirtualFile> resolveVirtualFileCollection(@NotNull String path,
                                                                 @NotNull String basename,
                                                                 @NotNull GlobalSearchScope scope) {
        List<VirtualFile> suffixedVirtualFiles = new ArrayList<>();
        PsiFile[] projectFilesWithBaseName = FilenameIndex.getFilesByName(myProject, basename, scope);

        for (PsiFile projectFileWithBaseName : projectFilesWithBaseName) {
            VirtualFile virtualFile = projectFileWithBaseName.getVirtualFile();
            String virtualFilePath = virtualFile.getPath();

            if (virtualFilePath.endsWith(path)) {
                suffixedVirtualFiles.add(virtualFile);
            }
        }

        return suffixedVirtualFiles;
    }
}
