package org.elixir_lang.exunit;

import org.elixir_lang.jps.builder.ParametersList;
import org.elixir_lang.sdk.ElixirSdkRelease;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                                         @Nullable ElixirSdkRelease elixirSdkRelease) {

        String versionDirectory = "1.4.0";

        if (elixirSdkRelease != null && elixirSdkRelease.compareTo(ElixirSdkRelease.V_1_4) < 0) {
            versionDirectory = "1.1.0";
        }

        String source = versionDirectory + "/" + FORMATTER_FILE_NAME;

        relativeSourcePathList.add(source);
    }

    @NotNull
    private static List<File> copy(@Nullable ElixirSdkRelease elixirSdkRelease, boolean useCustomMixTask) throws IOException {
        return org.elixir_lang.ElixirModules.copy(BASE_PATH, relativeSourcePathList(elixirSdkRelease, useCustomMixTask));
    }

    @NotNull
    public static ParametersList parametersList(@Nullable ElixirSdkRelease elixirSdkRelease, boolean useCustomMixTask) throws IOException {
        return org.elixir_lang.ElixirModules.parametersList(copy(elixirSdkRelease, useCustomMixTask));
    }

    private static List<String> relativeSourcePathList(@Nullable ElixirSdkRelease elixirSdkRelease,
                                                       boolean useCustomMixTask) {
        List<String> relativeSourcePathList = new ArrayList<>();
        relativeSourcePathList.add(FORMATTING_FILE_NAME);

        addFormatterPath(relativeSourcePathList, elixirSdkRelease);
        addCustomMixTask(relativeSourcePathList, useCustomMixTask);

        return relativeSourcePathList;
    }
}
