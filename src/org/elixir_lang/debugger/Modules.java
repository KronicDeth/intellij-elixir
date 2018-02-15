package org.elixir_lang.debugger;

import org.elixir_lang.jps.builder.ParametersList;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Modules {
    private static final String BASE_PATH = "/debugger";
    private static final String INTELLIJ_ELIXIR_DEBUG_SERVER = "lib/debug_server.ex";
    private static final String MIX_TASKS_INTELLIJ_ELIXIR_DEBUG_TASK = "lib/debug_task.ex";
    private static final List<String> RELATIVE_SOURCE_PATH_LIST = Arrays.asList(
            INTELLIJ_ELIXIR_DEBUG_SERVER,
            MIX_TASKS_INTELLIJ_ELIXIR_DEBUG_TASK
    );

    private Modules() {
    }

    @NotNull
    public static ParametersList add(@NotNull ParametersList parametersList) throws IOException {
        return org.elixir_lang.ElixirModules.add(parametersList, copy());
    }

    @NotNull
    private static List<File> copy() throws IOException {
        return org.elixir_lang.ElixirModules.copy(BASE_PATH, RELATIVE_SOURCE_PATH_LIST);
    }
}
