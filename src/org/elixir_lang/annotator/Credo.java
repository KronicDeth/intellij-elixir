package org.elixir_lang.annotator;

import com.google.common.base.Joiner;
import com.intellij.diagnostic.LogMessageEx;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.util.ExecUtil;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.diagnostic.Attachment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import gnu.trove.THashMap;
import org.elixir_lang.jps.builder.ParametersList;
import org.elixir_lang.mix.runner.MixRunningStateUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;
import java.util.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
import static org.elixir_lang.mix.runner.MixRunningStateUtil.workingDirectory;

// See https://github.com/antlr/jetbrains-plugin-sample/blob/7c400e02f89477dbe179123a2d43f839b4df05d7/src/java/org/antlr/jetbrains/sample/SampleExternalAnnotator.java
public class Credo extends ExternalAnnotator<PsiFile, List<Credo.Issue>> {
    private static final Pattern LINE_PATTERN = Pattern.compile("(?<path>.+?):(?<line>\\d+):(?:(?<column>\\d+):)? (?<tag>[CFRSW]): (?<message>.+)");
    private static final Pattern EXPLANATION_LINE_PATTERN = Pattern.compile("┃ (?<content>.*)");
    private static final Pattern EXPLAINABLE_PATTERN = Pattern.compile("\\s*(?<explainable>(?<path>.+\\.exs?):(?<lineNumber>\\d+)(:?:(?<columnNumber>\\d+))?)");
    private static final Pattern HEADER_PATTERN = Pattern.compile("^\\s*__ (?<header>.+)");
    private static final String CODE_IN_QUESTION_HEADER = "CODE IN QUESTION";
    private static final String CONFIGURATION_OPTIONS = "CONFIGURATION OPTIONS";
    private static final String WHY_IT_MATTERS_HEADER = "WHY IT MATTERS";
    public static final Logger LOGGER = Logger.getInstance(Credo.class);
    public static final String INDENT = "     ";

    @NotNull
    private static List<Issue> lineListToIssueList(@NotNull List<String> lineList, @NotNull Project project) {
        List<Issue> issueList;

        if (!lineList.isEmpty()) {
            issueList = new ArrayList<>();

            for (String line : lineList) {
                Issue issue = lineToIssue(line, project);

                if (issue != null) {
                    issueList.add(issue);
                }
            }
        } else {
            issueList = Collections.emptyList();
        }

        return issueList;
    }

    @Nullable
    private static Issue lineToIssue(@NotNull String line, @NotNull Project project) {
        Matcher matcher = LINE_PATTERN.matcher(line);
        Issue issue;

        if (matcher.matches()) {
            int lineNumber = Integer.parseInt(matcher.group("line"));
            @Nullable String formattedColumn = matcher.group("column");
            @Nullable Integer column;

            if (formattedColumn != null) {
                column = Integer.parseInt(formattedColumn);
            } else {
                column = null;
            }

            Issue.Check check = Issue.Check.checkByTag.get(matcher.group("tag"));
            String message = matcher.group("message");

            issue = new Issue(matcher.group("path"), lineNumber - 1, column, check, message);
            issue.putExplanation(project);
        } else {
            issue = null;
        }

        return issue;
    }

    @NotNull
    private static GeneralCommandLine generalCommandLine(@NotNull GeneralCommandLine workingDirectoryGeneralCommandLine,
                                                  @NotNull Project project,
                                                  @NotNull ParametersList mixParametersList) {
        return MixRunningStateUtil.commandLine(
                workingDirectoryGeneralCommandLine,
                project,
                new ParametersList(),
                mixParametersList
        );
    }

    @NotNull
    private static ParametersList mixParametersList() {
        ParametersList parametersList = new ParametersList();
        parametersList.add("credo");

        return parametersList;
    }

    @NotNull
    private static ParametersList mixParametersList(@NotNull Issue issue) {
        StringBuilder explainableBuilder = new StringBuilder(issue.path).append(':').append(issue.line + 1);

        if (issue.column != null) {
            explainableBuilder.append(':').append(issue.column);
        }

        return mixParametersList(explainableBuilder.toString());
    }

    @NotNull
    private static ParametersList mixParametersList(@NotNull String explainable) {
        ParametersList parametersList = mixParametersList();
        parametersList.add(explainable);

        return parametersList;
    }

    @NotNull
    private static GeneralCommandLine generalCommandLine(@NotNull Project project, @NotNull ParametersList mixParametersList) {
        GeneralCommandLine workingDirectoryGeneralCommandLine = new GeneralCommandLine();

        String workingDirectory = workingDirectory(project);
        workingDirectoryGeneralCommandLine.withWorkDirectory(workingDirectory);

        return generalCommandLine(
                workingDirectoryGeneralCommandLine,
                project,
                mixParametersList
        );
    }

    @NotNull
    private static String navigationHref(@NotNull String workingDirectory, @NotNull Matcher matcher) {
        String relativePath = matcher.group("path");
        String path = Paths.get(workingDirectory, relativePath).toString();
        StringBuilder hrefBuilder = new StringBuilder("#navigation/").append(path);

        return offset(path, matcher)
                .map(offset -> hrefBuilder.append(':').append(offset))
                .orElse(hrefBuilder).toString();
    }

    @NotNull
    private static Optional<Integer> offset(@NotNull String path, @NotNull Matcher matcher) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(path);
        Optional<Integer> offset = Optional.empty();

        if (virtualFile != null) {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

            if (document != null) {
                int lineNumber = Integer.parseInt(matcher.group("lineNumber"));
                int start = document.getLineStartOffset(lineNumber - 1);

                @Nullable String formattedColumnNumber = matcher.group("columnNumber");

                if (formattedColumnNumber != null) {
                    int column = Integer.parseInt(formattedColumnNumber) - 1;
                    offset = Optional.of(start + column);
                } else {
                    offset = Optional.of(start);
                }
            }
        }

        return offset;
    }

    @NotNull
    private static String stripEdge(@NotNull String explanationLine) {
        Matcher matcher = EXPLANATION_LINE_PATTERN.matcher(explanationLine);
        String stripped;

        if (matcher.matches()) {
            stripped = matcher.group("content");
        } else {
            final String title = "Edge could not be stripped from explanation line";
            LOGGER.error(LogMessageEx.createEvent(
                    title + "\n" +
                            "\n" +
                            "`" +explanationLine + "`\n" ,
                    Joiner.on("\n").join(new Throwable().getStackTrace()),
                    title,
                    null,
                    (Attachment) null
                    )
            );

            stripped = explanationLine;
        }

        return stripped;
    }

    @NotNull
    private static String preludeContentLineToHTMLLine(@NotNull String contentLine, @NotNull String workingDirectory) {
        Matcher explainableMatcher = EXPLAINABLE_PATTERN.matcher(contentLine);
        String htmlLine;

        if (explainableMatcher.find()) {
            String explainable = explainableMatcher.group("explainable");
            int before = explainableMatcher.start("explainable");
            int after = explainableMatcher.end("explainable");
            htmlLine = escapeHtml(contentLine.substring(0, before)) +
                    "<a href=\"" + navigationHref(workingDirectory, explainableMatcher) + "\">" +
                    explainable +
                    "</a>" +
                    escapeHtml(contentLine.substring(after));
        } else {
            htmlLine = escapeHtml(contentLine);
        }

        return htmlLine + "<br/>";
    }

    @Nullable
    @Override
    public List<Issue> doAnnotate(PsiFile file) {
        List<Issue> issueList;

        try {
            ProcessOutput processOutput = ExecUtil.execAndGetOutput(generalCommandLine(file));

            issueList = lineListToIssueList(processOutput.getStdoutLines(), file.getProject());
        } catch (ExecutionException executionException) {
            issueList = Collections.emptyList();
        }

        return issueList;
    }

    @NotNull
    private ParametersList mixParametersList(@NotNull PsiFile file) {
        ParametersList parametersList = mixParametersList();
        parametersList.add("--format");
        parametersList.add("flycheck");
        parametersList.add(file.getVirtualFile().getPath());

        return parametersList;
    }

    @NotNull
    private GeneralCommandLine generalCommandLine(@NotNull PsiFile file) {
        return generalCommandLine(
                file.getProject(),
                mixParametersList(file)
        );
    }

    @Override
    public void apply(@NotNull PsiFile file, @NotNull List<Issue> issueList, @NotNull AnnotationHolder holder) {
        if (issueList.size() > 0) {
            @Nullable Document document = file.getViewProvider().getDocument();

            if (document != null) {
                String workingDirectory = workingDirectory(file.getProject());

                for (Issue issue : issueList) {
                    int lineStartOffset = document.getLineStartOffset(issue.line);
                    int start;
                    int end;

                    if (issue.column != null) {
                        start = lineStartOffset + issue.column;
                        end = start + 1;
                    } else {
                        start = lineStartOffset;
                        end = document.getLineEndOffset(issue.line);
                    }

                    Annotation annotation = holder.createWarningAnnotation(new TextRange(start, end), issue.message);
                    issue.explanation.ifPresent(
                            explanation -> annotation.setTooltip(explanationToToolTip(explanation, workingDirectory))
                    );
                }
            }
        }
    }

    @Contract(pure = true)
    @NotNull
    private String explanationToToolTip(@NotNull Stream<String> explanation, @NotNull String workingDirectory) {
        List<String> headers = new ArrayList<>();
        Map<String, List<String>> contentsByHeader = new HashMap<>();
        Ref<String> headerRef = Ref.create();
        headers.add(headerRef.get());

        explanation.map(Credo::stripEdge).forEachOrdered(content -> {
            Matcher headerMatcher = HEADER_PATTERN.matcher(content);

            if (headerMatcher.matches()) {
                String header = headerMatcher.group("header");
                headers.add(header);
                headerRef.set(header);
            } else {
                contentsByHeader.computeIfAbsent(headerRef.get(), k -> new ArrayList<>()).add(content);
            }
        });

        return headers
                .stream()
                .flatMap(header -> sectionToHTML(header, contentsByHeader.get(header), workingDirectory))
                .collect(Collectors.joining("\n"));
    }

    private Stream<String> sectionToHTML(@Nullable String header,
                                         @NotNull List<String> contentLineList,
                                         @NotNull String workingDirectory) {
        Stream<String> headerHTMLLineStream = headerHTMLLineStream(header);
        Stream<String> contentHTMLLineStream;

        if (header == null) {
            contentHTMLLineStream = preludeContentLineListToHTMLLineStream(contentLineList, workingDirectory);
        } else if (header.equals(CODE_IN_QUESTION_HEADER)) {
            contentHTMLLineStream = codeInQuestionContentLineListToHTMLLineStream(contentLineList);
        } else if (header.equals(CONFIGURATION_OPTIONS) || header.equals(WHY_IT_MATTERS_HEADER)) {
            contentHTMLLineStream = contentLineList.stream().map(contentLine -> {
                String unindentedLine;

                if (contentLine.isEmpty()) {
                    unindentedLine = contentLine;
                } else if (contentLine.startsWith(INDENT)) {
                    unindentedLine = contentLine.substring(INDENT.length());
                } else {
                    final String title = header + " content line was neither blank nor starting with " +
                            INDENT.length() + " spaces";
                    LOGGER.error(LogMessageEx.createEvent(
                            title + "\n" +
                                    "\n" +
                                    "`" + contentLine + "`\n",
                            Joiner.on("\n").join(new Throwable().getStackTrace()),
                            title,
                            null,
                            (Attachment) null
                    ));

                    unindentedLine = contentLine;
                }

                return unindentedLine;
            }).map(unindentedLine -> escapeHtml(unindentedLine) + "<br/>");
        } else {
            contentHTMLLineStream = Stream.empty();
        }

        return Stream.concat(headerHTMLLineStream, contentHTMLLineStream);
    }

    @NotNull
    private Stream<String> codeInQuestionContentLineListToHTMLLineStream(List<String> contentLineList) {
        Stream<String> unindentedLines = contentLineList.stream().map(indentedLine -> {
            String unindentedLine;

            if (indentedLine.length() == 0) {
                unindentedLine = indentedLine;
            } else {
                assert indentedLine.startsWith("   ");

                unindentedLine = indentedLine.substring(3);
            }

            return unindentedLine;
        });

        return Stream.concat(
                Stream.of("<pre><code>"),
                Stream.concat(unindentedLines, Stream.of("</code></pre>"))
        );
    }

    private static <T, R> Stream<R> withIndex(Stream<T> stream, FunctionWithIndex<T, R> function) {
        assert !stream.isParallel();

        final Iterator<T> iterator = stream.iterator();
        final Iterator<R> returnIterator = new Iterator<R>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public R next() {
                return function.apply(iterator.next(), index++);
            }
        };
        return iteratorToFiniteStream(returnIterator);
    }

    private static <T> Stream<T> iteratorToFiniteStream(Iterator<T> iterator) {
        final Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private Stream<String> preludeContentLineListToHTMLLineStream(@NotNull List<String> contentLineList,
                                                                  @NotNull String workingDirectory) {
        return withIndex(contentLineList.stream(), (contentLine, index) -> {
            String unindentedLine;

            if (index <= 1) {
                assert contentLine.startsWith("  ");

                unindentedLine = contentLine.substring(2);
            } else if (contentLine.length() == 0) {
                unindentedLine = contentLine;
            } else {
                assert contentLine.startsWith("     ");

                unindentedLine = contentLine.substring(5);
            }

            return unindentedLine;
        }).map(contentLine -> preludeContentLineToHTMLLine(contentLine, workingDirectory));
    }

    private Stream<String> headerHTMLLineStream(@Nullable String header) {
        Stream<String> htmlLineStream;

        if (header != null) {
            htmlLineStream = Stream.of("<h2>" + escapeHtml(header) + "</h2>");
        } else {
            htmlLineStream = Stream.empty();
        }

        return htmlLineStream;
    }

    @Override
    @NotNull
    public PsiFile collectInformation(@NotNull PsiFile file) {
        return file;
    }

    public static class Issue {
        public final int line;
        @NotNull
        final String path;
        @NotNull
        final Check check;
        @Nullable
        final Integer column;
        @NotNull
        final String message;
        @NotNull
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        Optional<Stream<String>> explanation = Optional.empty();

        Issue(@NotNull String path,
              int line,
              @Nullable Integer column,
              @NotNull Check check,
              @NotNull String message) {
            this.path = path;
            this.line = line;
            this.column = column;
            this.check = check;
            this.message = message;
        }

        @NotNull
        private Optional<Stream<String>> explanation(@NotNull Project project) {
            ParametersList mixParametersList = mixParametersList(this);
            GeneralCommandLine generalCommandLine = generalCommandLine(project, mixParametersList);
            Optional<Stream<String>> explanation;

            try {
                explanation = Optional.of(
                        ExecUtil
                                .execAndGetOutput(generalCommandLine)
                                .getStdoutLines(true)
                                .stream()
                                .skip(3)
                                .filter(line -> !line.isEmpty())
                );
            } catch (ExecutionException executionException) {
                explanation = Optional.empty();
            }

            return explanation;
        }

        void putExplanation(@NotNull Project project) {
            this.explanation = explanation(project);
        }

        public enum Check {
            // See https://github.com/rrrene/credo/blob/8a8a9c6da354d96d2be8507db9e738147ae1ccb2/lib/credo/cli/output.ex#L5-L32 for tags
            CONSISTENCY("Consistency", "C"),
            READABILITY("Readability", "R"),
            REFACTORING_OPPORTUNITY("Refactoring Opportunity", "F"),
            SOFTWARE_DESIGN("Software Design", "S"),
            WARNINGS("Warnings", "W");

            public static final Map<String, Check> checkByTag;

            static {
                checkByTag = new THashMap<>();

                for (Check check : Check.values()) {
                    checkByTag.put(check.tag, check);
                }
            }

            public final String category;
            public final String tag;

            Check(@NotNull String category, @NotNull String tag) {
                this.category = category;
                this.tag = tag;
            }
        }

    }
}
