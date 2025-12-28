package org.elixir_lang.run

import com.intellij.openapi.diagnostic.Logger
import kotlinx.coroutines.runBlocking

/**
 * Handles graceful termination of Elixir/Erlang processes using double SIGINT.
 *
 * The Erlang VM (BEAM) treats the first SIGINT (Ctrl-C) as entering BREAK mode, displaying
 * a menu with options to halt the VM, and requires a second SIGINT to actually exit.
 * This interface implements that behavior for clean process shutdown.
 *
 * ## Why Double SIGINT?
 *
 * Elixir/Erlang processes need graceful termination to:
 * - Flush buffered log output
 * - Complete in-flight operations
 * - Clean up resources (file handles, network connections)
 * - Trigger supervision tree shutdowns
 *
 * A single SIGKILL or hard kill can leave resources in inconsistent states.
 *
 * ## Implementation Strategy
 *
 * Implementations should attempt termination in this order:
 * 1. WSL/IJent-specific interrupt (for remote processes)
 * 2. PID-based SIGINT via kill command
 * 3. Caller-provided fallback (typically `super.destroyProcessImpl()`)
 *
 * @see ElixirDoubleSignalTerminator
 */
interface DoubleSignalTerminator {
    /**
     * Performs double-SIGINT termination on the given process.
     *
     * Sends two SIGINT signals with a brief delay (typically 200ms) between them to allow
     * the BEAM BREAK menu to appear before the final exit.
     *
     * @param process The process to terminate gracefully
     * @param fallback Called if double-SIGINT fails or is unavailable. Typically delegates
     *                 to the parent class's destroyProcessImpl() for platform-specific
     *                 termination (e.g., Process.destroy() or SIGKILL)
     */
    fun performDoubleSignalTermination(process: Process, fallback: () -> Unit)
}

class ElixirDoubleSignalTerminator : DoubleSignalTerminator {
    private val log = Logger.getInstance(ElixirDoubleSignalTerminator::class.java)
    private var doubleSigintInProgress = false

    override fun performDoubleSignalTermination(process: Process, fallback: () -> Unit) {
        log.info("performDoubleSignalTermination called, doubleSigintInProgress=$doubleSigintInProgress")

        // Prevent infinite recursion
        if (doubleSigintInProgress) {
            log.warn("Already in double-SIGINT mode, calling fallback")
            fallback()
            return
        }

        try {
            doubleSigintInProgress = true

            if (!process.isAlive) {
                log.info("Process already terminated")
                return
            }

            log.info("Process is alive: ${process.isAlive}, attempting first SIGINT")

            // Send first SIGINT
            val firstResult = sendSignal(process, fallback)
            log.info("First SIGINT result: $firstResult")

            if (!firstResult) {
                log.warn("First SIGINT failed, calling fallback")
                fallback()
                return
            }

            // Wait briefly for the BREAK menu to appear and give process a chance to exit.
            // Using waitFor() instead of Thread.sleep() allows early return if process exits quickly.
            log.info("Waiting up to 200ms for process to respond to first SIGINT...")
            val terminatedEarly = process.waitFor(200, java.util.concurrent.TimeUnit.MILLISECONDS)

            // Check if process is still alive
            if (terminatedEarly || !process.isAlive) {
                log.info("Process terminated after first SIGINT")
                return
            }

            log.info("Process still alive after first SIGINT, sending second SIGINT")

            // Send second SIGINT
            val secondResult = sendSignal(process, fallback)
            log.info("Second SIGINT result: $secondResult")

            if (!secondResult) {
                log.warn("Second SIGINT failed, calling fallback")
                fallback()
            } else {
                log.info("Double SIGINT completed successfully")
            }
        } catch (e: Exception) {
            log.error("Exception during double-SIGINT termination: ${e.message}", e)
            fallback()
        } finally {
            doubleSigintInProgress = false
        }
    }

    /**
     * Sends a single SIGINT signal to the process using the most appropriate method.
     *
     * Attempts termination strategies in order of preference:
     * 1. **IJent/WSL interrupt**: For processes running in WSL2 or remote environments via IntelliJ's
     *    IJent (IntelliJ Agent) framework. Uses EelProcess.interrupt() which properly handles
     *    cross-environment signaling.
     * 2. **PID-based kill**: For native processes where we can access the Unix PID via reflection.
     *    Directly sends SIGINT (signal 2) using the `kill` command.
     * 3. **Fallback strategy**: Uses the caller-provided fallback, typically the parent class's
     *    destroyProcessImpl() which may use Process.destroy() or platform-specific termination.
     *
     * @param process The process to send a signal to
     * @param fallback Termination strategy to use if IJent and PID-based approaches fail
     * @return true if signal was sent successfully, false otherwise
     */
    private fun sendSignal(process: Process, fallback: () -> Unit): Boolean {
        // Try IJent-aware approach first (WSL2/remote processes)
        if (tryIjentInterrupt(process)) {
            return true
        }

        // Try PID-based kill command (native Unix processes)
        if (tryPidKill(process)) {
            return true
        }

        // Use caller's fallback (typically super.destroyProcessImpl() or process.destroy())
        return try {
            log.info("Using provided fallback termination strategy")
            fallback()
            true
        } catch (e: Exception) {
            log.error("Fallback termination failed: ${e.message}", e)
            false
        }
    }

    private fun tryIjentInterrupt(process: Process): Boolean {
        try {
            // Check if this is an IJent wrapper process
            if (!process.javaClass.name.contains("IjentChildProcessAdapter")) {
                return false
            }

            log.info("Detected IjentChildProcessAdapter, using EelProcess.interrupt()")

            // Access the internal ijentChildProcess field (EelProcess)
            val ijentChildProcessField = process.javaClass.getDeclaredField("ijentChildProcess")
            ijentChildProcessField.isAccessible = true
            val ijentChildProcess = ijentChildProcessField.get(process)

            if (ijentChildProcess != null) {
                log.info("Got ijentChildProcess, calling interrupt()")

                // Cast to EelProcess and call interrupt()
                val eelProcess = ijentChildProcess as? com.intellij.platform.eel.EelProcess
                if (eelProcess != null) {
                    runBlocking {
                        try {
                            eelProcess.interrupt()
                            log.info("interrupt() completed successfully")
                        } catch (e: Exception) {
                            log.error("interrupt() failed: ${e.message}", e)
                            throw e
                        }
                    }
                    return true
                } else {
                    log.warn("Could not cast to EelProcess")
                }
            }
        } catch (e: Exception) {
            log.error("Failed to call EelProcess.interrupt(): ${e.message}", e)
        }
        return false
    }

    private fun tryPidKill(process: Process): Boolean {
        try {
            // Try to get the PID using reflection
            log.info("Getting PID via reflection from ${process.javaClass.name}")
            val pidField = process.javaClass.getDeclaredField("pid")
            pidField.isAccessible = true
            val pid = pidField.get(process)
            log.info("PID value: $pid (type: ${pid?.javaClass?.name})")

            val pidLong = when (pid) {
                is Long -> pid
                is Int -> pid.toLong()
                else -> {
                    log.warn("Unexpected PID type: ${pid?.javaClass?.name}")
                    return false
                }
            }

            log.info("Sending SIGINT (signal 2) to PID $pidLong")

            // Send signal using kill command
            val killProcess = Runtime.getRuntime().exec(arrayOf("kill", "-2", pidLong.toString()))
            val exitCode = killProcess.waitFor()

            log.info("kill command exit code: $exitCode")
            return exitCode == 0
        } catch (e: NoSuchFieldException) {
            log.debug("Could not find 'pid' field in ${process.javaClass.name}", e)
            return false
        } catch (e: Exception) {
            log.error("Exception in tryPidKill: ${e.message}", e)
            return false
        }
    }
}
