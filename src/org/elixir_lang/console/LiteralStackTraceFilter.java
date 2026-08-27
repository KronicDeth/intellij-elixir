package org.elixir_lang.console;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.filters.HyperlinkInfoFactory;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Links the {@code file:} and {@code line:} pair that Elixir prints for a stack trace entry when the
 * trace appears inside an inspected term rather than as a formatted trace, as in
 * {@code {Gald.Phase, :initialize_screen, 3, [file: 'lib/gald/phase.ex', line: 75]}}.
 *
 * <p>{@link FileReferenceFilter} covers the formatted {@code (app) path:line:} frames and cannot
 * cover these: its expression wants the digits immediately after the colon, while a keyword list is
 * printed as {@code line: 75}, with a space. The two therefore never match the same text.
 *
 * <p>{@code inspect} chooses between two shapes by width, and both have to be read:
 *
 * <ul>
 *   <li>the whole entry on one line, which is what a {@code Logger} report contains - and one such
 *       line can carry several entries, so every match on it becomes its own link;</li>
 *   <li>the keyword list broken one key per line, which is what {@code IO.inspect(__STACKTRACE__)}
 *       produces at the default width. {@code file:} and {@code line:} then land on different
 *       lines, and a filter is given one line at a time.</li>
 * </ul>
 *
 * <h3>Why the broken shape is handled two different ways</h3>
 *
 * <p>What a filter can do about the second shape depends on which console is asking, and the two
 * consoles this plugin attaches to disagree:
 *
 * <ul>
 *   <li>{@code ConsoleViewImpl} - the Mix, Elixir, Distillery and test-runner consoles - calls
 *       through {@code AsyncFilterRunner}, whose offsets run through the whole console document.
 *       Consecutive lines are handed over in order and abut exactly, so the path can be carried to
 *       the line that holds the number and linked there, precisely. Lines are sometimes handed over
 *       more than once, so carrying has to be idempotent, and can be skipped when the document
 *       changes underneath, so it must not be the only chance to link.</li>
 *   <li>{@code TerminalExecutionConsole} - the IEx consoles - calls through
 *       {@code JediTermHyperlinkFilterAdapter}, which passes {@code applyFilter(line, line.length)}.
 *       Offsets are relative to the line, so a result can only highlight text inside the line it was
 *       given; carrying anything forward would apply one line's offsets to another. There the path
 *       is linked to the top of the file instead, and the number stays legible on the next line.</li>
 * </ul>
 *
 * <p>The two are told apart by {@code lineStartOffset > 0} rather than by the console's class. That
 * is the invariant the carrying actually depends on - offsets that mean something outside this line
 * - it is zero in the terminal by construction, and a console added later that behaves like the
 * terminal gets the safe branch without anyone having to notice it exists.
 */
final class LiteralStackTraceFilter implements Filter {
    /**
     * The path and its number, together on one line. A stack trace's {@code file:} is a charlist, so
     * Elixir 1.14 and earlier inspect it as {@code 'lib/x.ex'} and 1.15 and later as the sigil
     * {@code ~c"lib/x.ex"}. The double quote is admitted for the sigil's sake, which incidentally
     * accepts a binary {@code "lib/x.ex"} - not a form the language is known to produce, and not
     * one any test asserts.
     *
     * <p>The backreference keeps the closing quote the same as the opening one, and excluding quotes
     * from the path stops a match running from one entry's {@code file:} to a later entry's closing
     * quote - which a lazy {@code .+?} does as soon as the pattern it is inside cannot be satisfied
     * nearer.
     */
    private static final Pattern WHOLE_ENTRY =
            Pattern.compile("\\bfile:\\s*(?:~c)?([\"'])([^\"']+)\\1\\s*,\\s*line:\\s*([0-9]+)");

    /** A path that ends its line, the number having been wrapped onto the next one. */
    private static final Pattern WRAPPED_PATH =
            Pattern.compile("\\bfile:\\s*(?:~c)?([\"'])([^\"']+)\\1\\s*,\\s*$");

    /** The number that continues a {@link #WRAPPED_PATH}. */
    private static final Pattern WRAPPED_LINE = Pattern.compile("^\\s*line:\\s*([0-9]+)");

    private static final int PATH_GROUP = 2;
    private static final int LINE_GROUP = 3;

    /** Where a path is linked when its number is out of reach. */
    private static final int FIRST_LINE = 0;

    private final Project project;

    /**
     * The path from the previous line, waiting for its number. Only ever set where offsets run
     * through the document. Written and read from whichever pooled thread the console filters on -
     * one at a time, since {@code AsyncFilterRunner} runs every console's filtering through a single
     * sequential executor - so a volatile field is enough, and losing a race would cost one link
     * rather than misplace one, because {@link WrappedPath#isFollowedBy} still has to agree.
     */
    private volatile WrappedPath wrapped;

    LiteralStackTraceFilter(@NotNull Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public Result applyFilter(@NotNull String line, int entireLength) {
        int lineStartOffset = entireLength - line.length();
        WrappedPath carried = wrapped;
        // Spent or invalidated either way: a line that does not continue it ends its life.
        wrapped = null;

        List<ResultItem> resultItems = numberContinuing(carried, line, lineStartOffset);
        resultItems = wholeEntriesOn(line, lineStartOffset, resultItems);
        resultItems = wrappedPathOn(line, lineStartOffset, entireLength, resultItems);

        if (resultItems == null) {
            return null;
        }

        Result result = new Result(resultItems);
        // Belt and braces. A composite filter stops at the first filter to return a result unless it
        // was built with setForceUseAllFilters(true), which every console this plugin attaches to
        // does today. Saying so anyway costs a line and keeps FileReferenceFilter's links on a line
        // holding both forms if one ever does not.
        result.setNextAction(NextAction.CONTINUE_FILTERING);

        return result;
    }

    /**
     * The link for a path carried from the previous line, now that this line supplies its number.
     * The highlight is back on that previous line, which only means anything because the offsets
     * involved run through the document rather than the line.
     */
    @Nullable
    private List<ResultItem> numberContinuing(@Nullable WrappedPath carried,
                                              @NotNull String line,
                                              int lineStartOffset) {
        if (carried == null || !carried.isFollowedBy(lineStartOffset)) {
            return null;
        }

        Matcher matcher = WRAPPED_LINE.matcher(line);

        if (!matcher.find()) {
            return null;
        }

        return link(
                carried.path,
                carried.pathStartOffset,
                carried.pathEndOffset,
                zeroBasedLine(matcher.group(1)),
                null
        );
    }

    @Nullable
    private List<ResultItem> wholeEntriesOn(@NotNull String line,
                                            int lineStartOffset,
                                            @Nullable List<ResultItem> resultItems) {
        Matcher matcher = WHOLE_ENTRY.matcher(line);

        while (matcher.find()) {
            resultItems = link(
                    matcher.group(PATH_GROUP),
                    lineStartOffset + matcher.start(PATH_GROUP),
                    lineStartOffset + matcher.end(PATH_GROUP),
                    zeroBasedLine(matcher.group(LINE_GROUP)),
                    resultItems
            );
        }

        return resultItems;
    }

    /**
     * Carries a path whose number was wrapped onto the next line, where the offsets to carry mean
     * something outside this line; links it to the top of the file where they do not.
     *
     * <p>Re-reading the same line, which the console does, simply carries the same path again.
     */
    @Nullable
    private List<ResultItem> wrappedPathOn(@NotNull String line,
                                           int lineStartOffset,
                                           int entireLength,
                                           @Nullable List<ResultItem> resultItems) {
        Matcher matcher = WRAPPED_PATH.matcher(line);

        if (!matcher.find()) {
            return resultItems;
        }

        String path = matcher.group(PATH_GROUP);
        int pathStartOffset = lineStartOffset + matcher.start(PATH_GROUP);
        int pathEndOffset = lineStartOffset + matcher.end(PATH_GROUP);

        if (lineStartOffset > 0) {
            wrapped = new WrappedPath(path, pathStartOffset, pathEndOffset, entireLength);

            return resultItems;
        }

        return link(path, pathStartOffset, pathEndOffset, FIRST_LINE, resultItems);
    }

    /**
     * Appends a link for {@code path} over the given offsets, or returns {@code resultItems}
     * untouched when the path names nothing that is indexed - a dependency the project model never
     * attached, or a path an install records but does not ship, such as Elixir's own
     * {@code src/elixir.erl}.
     *
     * <p>Several candidates for one path become a single link offering the choice, rather than
     * stacked links over the same text.
     */
    @Nullable
    private List<ResultItem> link(@NotNull String path,
                                  int highlightStartOffset,
                                  int highlightEndOffset,
                                  int zeroBasedLine,
                                  @Nullable List<ResultItem> resultItems) {
        Collection<VirtualFile> virtualFiles = SourceFileResolver.resolve(project, path);

        if (virtualFiles.isEmpty()) {
            return resultItems;
        }

        if (resultItems == null) {
            resultItems = new ArrayList<>();
        }

        // Only the quoted path is highlighted: the number navigates, but underlining it would
        // extend the link across the `, line: ` between the two - or across a newline.
        resultItems.add(
                new AboveGenericFileLinks(
                        highlightStartOffset,
                        highlightEndOffset,
                        hyperlinkInfo(virtualFiles, zeroBasedLine)
                )
        );

        return resultItems;
    }

    /**
     * A link that outranks the terminal's own bare-path link over the same text.
     *
     * <p>The reworked terminal registers {@code TerminalGenericFileFilter} ahead of every plugin's
     * filters and builds the composite with {@code setForceUseAllFilters(true)}, so both run and both
     * mark up the same path. Its link is an invisible one - Ctrl to follow - and opens the file at
     * line 1, because all it sees is a path with no {@code :line} after it. Ours knows the number
     * from the keyword list beside it.
     *
     * <p>Both default to {@link HighlighterLayer#HYPERLINK}, and an exact-range tie goes to whichever
     * was added first, which is the terminal's. {@link FileReferenceFilter} never notices because its
     * match covers {@code path:line} and so is a wider range than the bare path. One layer up is
     * enough.
     *
     * <p>It applies to every link this filter makes, including a wrapped path linked to the top of
     * the file, where the terminal's own link would have gone to the same place. Distinguishing the
     * two would buy nothing and would make the layer depend on which branch produced the link.
     */
    private static final class AboveGenericFileLinks extends ResultItem {
        private AboveGenericFileLinks(int highlightStartOffset,
                                      int highlightEndOffset,
                                      @NotNull HyperlinkInfo hyperlinkInfo) {
            super(highlightStartOffset, highlightEndOffset, hyperlinkInfo);
        }

        @Override
        public int getHighlighterLayer() {
            return HighlighterLayer.HYPERLINK + 1;
        }
    }

    /**
     * A plain {@link OpenFileHyperlinkInfo} for the ordinary single-candidate case, and the chooser
     * only where a path really does name several files.
     *
     * <p>Not just brevity: the reworked terminal navigates by type, taking the line from the
     * descriptor for a {@code FileHyperlinkInfoBase} and falling back to a generic path for anything
     * else. {@code MultipleFilesHyperlinkInfo} implements {@code FileHyperlinkInfo} but does not
     * extend that base, so a single-file link built through the factory opened at line 1 there while
     * the same file linked by {@link FileReferenceFilter} - which uses {@code OpenFileHyperlinkInfo}
     * - opened at the right line. Every other console navigates through the info's own
     * {@code navigate} and cannot tell the two apart.
     */
    @NotNull
    private HyperlinkInfo hyperlinkInfo(@NotNull Collection<VirtualFile> virtualFiles, int zeroBasedLine) {
        if (virtualFiles.size() == 1) {
            return new OpenFileHyperlinkInfo(project, virtualFiles.iterator().next(), zeroBasedLine);
        }

        return HyperlinkInfoFactory
                .getInstance()
                .createMultipleFilesHyperlinkInfo(new ArrayList<>(virtualFiles), zeroBasedLine, project);
    }

    /**
     * Elixir reports a 1-based line; {@code OpenFileDescriptor} wants a 0-based one. A line number
     * too large for an {@code int} navigates to the top of the file rather than failing the whole
     * line's filtering.
     */
    private static int zeroBasedLine(@NotNull String digits) {
        int oneBasedLine;

        try {
            oneBasedLine = Integer.parseInt(digits);
        } catch (NumberFormatException ignored) {
            return FIRST_LINE;
        }

        return oneBasedLine > 0 ? oneBasedLine - 1 : FIRST_LINE;
    }

    private static final class WrappedPath {
        /**
         * How far past the end of the carrying line the next line may start. The two document-backed
         * pipelines disagree by one: {@code AsyncFilterRunner} includes a line's terminator in the
         * text it hands over, so the next line starts exactly on the end it reported, while
         * {@code Filter.applyToLineRange} excludes it and the next line starts one past. Two lines
         * never sit this close for any other reason.
         *
         * <p>Only a line that *follows* qualifies. {@code applyToLineRange} iterates backwards when
         * its range is reversed, and a path carried into an earlier line would be a link on text
         * that is not the path.
         */
        private static final int MAX_GAP = 1;

        private final String path;
        private final int pathStartOffset;
        private final int pathEndOffset;
        private final int lineEndOffset;

        private WrappedPath(@NotNull String path, int pathStartOffset, int pathEndOffset, int lineEndOffset) {
            this.path = path;
            this.pathStartOffset = pathStartOffset;
            this.pathEndOffset = pathEndOffset;
            this.lineEndOffset = lineEndOffset;
        }

        private boolean isFollowedBy(int lineStartOffset) {
            int gap = lineStartOffset - lineEndOffset;

            return gap >= 0 && gap <= MAX_GAP;
        }
    }
}
