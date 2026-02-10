package org.elixir_lang.jps.builder;

import org.elixir_lang.jps.builder.execution.GeneralCommandLine;
import org.elixir_lang.jps.builder.model.SdkProperties;
import org.elixir_lang.jps.builder.sdk_type.Elixir;
import org.elixir_lang.jps.shared.ElixirCliArgumentMerger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.library.sdk.JpsSdk;

import java.util.List;
import java.util.Map;

final class ElixirCliBase {
    private ElixirCliBase() {
    }

    @Nullable
    static List<String> dryRunArguments(@NotNull Map<String, String> environment,
                                        @Nullable String workingDirectory,
                                        @NotNull JpsSdk<SdkProperties> sdk,
                                        @NotNull List<String> erlArgumentList) {
        List<String> baseArguments =
                ElixirCliDryRun.baseArguments(environment, workingDirectory, sdk);
        return baseArguments != null ? ElixirCliArgumentMerger.mergeArguments(baseArguments, erlArgumentList) : null;
    }

    static void addFallbackArguments(@NotNull GeneralCommandLine commandLine,
                                     @NotNull List<String> erlArgumentList,
                                     @NotNull JpsSdk<SdkProperties> sdk) {
        addElixirFallbackArguments(commandLine, erlArgumentList);
        addMix(commandLine, sdk);
    }

    static void addElixirFallbackArguments(@NotNull GeneralCommandLine commandLine,
                                           @NotNull List<String> erlArgumentList) {
        commandLine.addParameters(erlArgumentList);
        commandLine.addParameters("-noshell", "-s", "elixir", "start_cli");
        commandLine.addParameters("-elixir", "ansi_enabled", "true");
        commandLine.addParameter("-extra");
    }

    private static void addMix(@NotNull GeneralCommandLine commandLine, @NotNull JpsSdk<SdkProperties> sdk) {
        String homePath = sdk.getHomePath();
        if (homePath == null) {
            return;
        }
        String mixPath = Elixir.mixFile(homePath).getPath();
        commandLine.addParameter(mixPath);
    }

}
