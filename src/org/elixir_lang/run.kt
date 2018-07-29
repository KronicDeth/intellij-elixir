package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.PtyCommandLine
import com.intellij.openapi.util.SystemInfo

fun commandLine(pty: Boolean, environment: Map<String, String>, workingDirectory: String?): GeneralCommandLine =
        commandLine(pty)
                .withCharset(Charsets.UTF_8)
                .withEnvironment(environment)
                .withWorkDirectory(workingDirectory)

private fun commandLine(pty: Boolean): GeneralCommandLine =
        if (pty) {
            PtyCommandLine().apply {
                if (!SystemInfo.isWindows) {
                    /* `tty_sl -c -e`'s `start_termcap` will fail if `TERM` is not set
                       (https://github.com/erlang/otp/blob/360b68d76d8c297d950616f088458b7c239be7ee/erts/emulator/drivers/unix/ttsl_drv.c#L1327-L1332) */
                    environment.putIfAbsent("TERM", "xterm-256color")
                }
            }
        } else {
            GeneralCommandLine()
        }
