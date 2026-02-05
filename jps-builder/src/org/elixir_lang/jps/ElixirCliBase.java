package org.elixir_lang.jps;

import org.elixir_lang.jps.builder.GeneralCommandLine;
import org.jetbrains.annotations.NotNull;

final class ElixirCliBase {
    private ElixirCliBase() {
    }

    static void addElixirBaseArguments(@NotNull GeneralCommandLine commandLine) {
        commandLine.addParameters("-noshell", "-s", "elixir", "start_cli");
        commandLine.addParameters("-elixir", "ansi_enabled", "true");
        commandLine.addParameter("-extra");
    }
}
