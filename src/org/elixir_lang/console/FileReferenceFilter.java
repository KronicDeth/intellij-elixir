package org.elixir_lang.console;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.InvalidExpressionException;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.StringPattern;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <a href="https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/console/FileReferenceFilter.java">...</a>
 */
public final class FileReferenceFilter implements Filter {
    static final String LINE_MACROS = "$LINE$";
    static final String PATH_MACROS = "$FILE_PATH$";
    /** The expression a formatted frame's {@code path:line} is read with. */
    static final String COMPILATION_ERROR_PATH = PATH_MACROS + ":" + LINE_MACROS;
    /**
     * Keeps the match linear on a line that cannot match: the lookbehind drops start offsets that
     * would only rescan the same run, and the possessive stops leading whitespace being re-split
     * against a class that also accepts a space.
     *
     * <p>The drive-letter prefix and lookahead exist because {@code :} is not in the path class, so
     * {@code C:} could otherwise be neither captured nor started from.
     *
     * <p>Assumes {@link #PATH_MACROS} opens the expression, as {@link #COMPILATION_ERROR_PATH} does.
     */
    private static final String FILE_PATH_REGEXP =
            "(?:(?<![\\s0-9 a-z_A-Z\\-\\\\./])|(?=[A-Za-z]:[\\\\/]))"
                    + "\\s*+((?:[A-Za-z]:)?[0-9 a-z_A-Z\\-\\\\./]+)";
    private static final String NUMBER_REGEXP = "([0-9]+)";

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

        if (filePathIndex == -1) {
            throw new InvalidExpressionException("Expression must contain " + PATH_MACROS + " marcos.");
        }

        // Both are required because the highlight runs from the path's group to the line's.
        if (lineIndex == -1) {
            throw new InvalidExpressionException("Expression must contain " + LINE_MACROS + " marcos.");
        }

        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(filePathIndex, PATH_MACROS);
        map.put(lineIndex, LINE_MACROS);
        String regex = StringUtil.replace(expression, PATH_MACROS, FILE_PATH_REGEXP);
        regex = StringUtil.replace(regex, LINE_MACROS, NUMBER_REGEXP);

        // The block below determines the registers based on the sorted map.
        int count = 0;
        for (Integer integer : map.keySet()) {
            count++;
            String s = map.get(integer);

            if (PATH_MACROS.equals(s)) {
                filePathIndex = count;
            } else if (LINE_MACROS.equals(s)) {
                lineIndex = count;
            }
        }

        myFileMatchGroup = filePathIndex;
        myLineMatchGroup = lineIndex;
        myPattern = Pattern.compile(regex, Pattern.MULTILINE);
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
    @Override
    public Result applyFilter(@NotNull String line, int entireLength) {
        // Matcher polls nothing itself, so the sequence it reads does, as RegexpFilter's does. The
        // cancellation is left to propagate so the non-blocking read action retries.
        Matcher matcher = myPattern.matcher(StringPattern.newBombedCharSequence(line));
        Result result = null;

        if (matcher.find()) {
            String filePath = matcher.group(myFileMatchGroup);
            Collection<VirtualFile> virtualFileCollection = SourceFileResolver.resolve(myProject, filePath);

            if (!virtualFileCollection.isEmpty()) {
                List<ResultItem> resultItemList = new ArrayList<>(virtualFileCollection.size());
                int lineStartOffset = entireLength - line.length();
                int highlightStartOffset = lineStartOffset + matcher.start(myFileMatchGroup);
                int highlightEndOffset = lineStartOffset + matcher.end(myLineMatchGroup);
                int fileLine = matchGroupToNumber(matcher, myLineMatchGroup);

                for (VirtualFile virtualFile : virtualFileCollection) {
                    resultItemList.add(
                            new AboveGenericFileLinks(
                                    highlightStartOffset,
                                    highlightEndOffset,
                                    new OpenFileHyperlinkInfo(myProject, virtualFile, fileLine)
                            )
                    );
                }

                result = new Result(resultItemList);
            }
        }

        return result;
    }
}
