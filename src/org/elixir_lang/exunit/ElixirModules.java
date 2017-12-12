package org.elixir_lang.exunit;

import org.elixir_lang.Level;
import org.elixir_lang.jps.builder.ParametersList;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.elixir_lang.Level.V_1_4;

public class ElixirModules {
    private static final String BASE_PATH = "exunit";
    private static final String FORMATTER_FILE_NAME = "team_city_ex_unit_formatter.ex";
    private static final String FORMATTING_FILE_NAME = "team_city_ex_unit_formatting.ex";
    private static final String MIX_TASK_FILE_NAME = "test_with_formatter.ex";

    private ElixirModules() {
    }

    private static void addCustomMixTask(@NotNull List<String> relativeSourcePathList, boolean useCustomMixTask) {
        if (useCustomMixTask) {
            relativeSourcePathList.add(MIX_TASK_FILE_NAME);
        }
    }

    private static void addFormatterPath(@NotNull List<String> relativeSourcePathList,
                                         @NotNull Level level) {
        String versionDirectory = "1.4.0";

        if (level.compareTo(V_1_4) < 0) {
            versionDirectory = "1.1.0";
        }

        String source = versionDirectory + "/" + FORMATTER_FILE_NAME;

        relativeSourcePathList.add(source);
    }

    @NotNull
    private static List<File> copy(@NotNull Level level, boolean useCustomMixTask) throws IOException {
        return org.elixir_lang.ElixirModules.copy(BASE_PATH, relativeSourcePathList(level, useCustomMixTask));
    }

    @NotNull
    public static ParametersList parametersList(@NotNull Level level, boolean useCustomMixTask) throws IOException {
        return org.elixir_lang.ElixirModules.parametersList(copy(level, useCustomMixTask));
    }

    private static List<String> relativeSourcePathList(@NotNull Level level, boolean useCustomMixTask) {
        List<String> relativeSourcePathList = new ArrayList<>();
        relativeSourcePathList.add(FORMATTING_FILE_NAME);

        addFormatterPath(relativeSourcePathList, level);
        addCustomMixTask(relativeSourcePathList, useCustomMixTask);

        return relativeSourcePathList;
    }
}
