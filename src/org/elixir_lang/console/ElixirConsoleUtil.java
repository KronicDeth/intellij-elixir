package org.elixir_lang.console;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/console/ErlangConsoleUtil.java
 */
public final class ElixirConsoleUtil {
  public static final String COMPILATION_ERROR_PATH = FileReferenceFilter.PATH_MACROS + ":" + FileReferenceFilter.LINE_MACROS;

  public ElixirConsoleUtil() {
  }

  public static void attachFilters(@NotNull Project project, @NotNull ConsoleView consoleView){
    // Formatted frames read `path:line`; a stack trace inspected as a term instead carries
    // `[file: ..., line: ...]`, which the compilation-error expression cannot match. Order does not
    // matter: every console this reaches builds its composite with setForceUseAllFilters(true), so
    // both run on every line and a line holding both forms gets both sets of links.
    consoleView.addMessageFilter(new LiteralStackTraceFilter(project));
    consoleView.addMessageFilter(new FileReferenceFilter(project, COMPILATION_ERROR_PATH));
  }
}
