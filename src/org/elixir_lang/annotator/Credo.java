package org.elixir_lang.annotator;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.util.ExecUtil;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import gnu.trove.THashMap;
import org.elixir_lang.mix.runner.MixRunConfiguration;
import org.elixir_lang.jps.builder.ParametersList;
import org.elixir_lang.mix.runner.MixRunningStateUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// See https://github.com/antlr/jetbrains-plugin-sample/blob/7c400e02f89477dbe179123a2d43f839b4df05d7/src/java/org/antlr/jetbrains/sample/SampleExternalAnnotator.java
public class Credo extends ExternalAnnotator<PsiFile, List<Credo.Issue>> {
    private static final Pattern LINE_PATTERN = Pattern.compile("(?<path>.+?):(?<line>\\d+):(?:(?<column>\\d+):)? (?<tag>[CFRSW]): (?<message>.+)");

    @NotNull
    private static List<Issue> lineListToIssueList(@NotNull List<String> lineList) {
        List<Issue> issueList;

        if (!lineList.isEmpty()) {
            issueList = new ArrayList<>();

            for (String line : lineList) {
                Issue issue = lineToIssue(line);

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
    private static Issue lineToIssue(@NotNull String line) {
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

            issue = new Issue(lineNumber - 1, column, check, message);
        } else {
            issue = null;
        }

        return issue;
    }

    @Nullable
    @Override
    public List<Issue> doAnnotate(PsiFile file) {
        List<Issue> issueList;

        try {
            ProcessOutput processOutput = ExecUtil.execAndGetOutput(generalCommandLine(file));

            issueList = lineListToIssueList(processOutput.getStdoutLines());
        } catch (ExecutionException executionException) {
            issueList = Collections.emptyList();
        }

        return issueList;
    }

    @NotNull
    private GeneralCommandLine generalCommandLine(@NotNull PsiFile file) {
        MixRunConfiguration mixRunConfiguration = new MixRunConfiguration("mix credo", file.getProject());
        ParametersList mixParametersList = new ParametersList();
        mixParametersList.add("credo");
        mixParametersList.add("--format");
        mixParametersList.add("flycheck");
        mixParametersList.add(file.getVirtualFile().getPath());

        ParametersList elixirParametersList = new ParametersList();

        return MixRunningStateUtil.commandLine(
                mixRunConfiguration,
                elixirParametersList,
                mixParametersList
        );
    }

    @Override
    public void apply(@NotNull PsiFile file, @NotNull List<Issue> issueList, @NotNull AnnotationHolder holder) {
        if (issueList.size() > 0) {
            @Nullable Document document = file.getViewProvider().getDocument();

            if (document != null) {
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

                    holder.createWarningAnnotation(new TextRange(start, end), issue.message);
                }
            }
        }
    }

    @Override
    @NotNull
    public PsiFile collectInformation(@NotNull PsiFile file) {
        return file;
    }

    public static class Issue {
        @NotNull
        public final Check check;
        public final int line;
        @Nullable
        public final Integer column;
        @NotNull
        public final String message;

        public Issue(int line, @Nullable Integer column, @NotNull Check check, @NotNull String message) {
            this.line = line;
            this.column = column;
            this.check = check;
            this.message = message;
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
