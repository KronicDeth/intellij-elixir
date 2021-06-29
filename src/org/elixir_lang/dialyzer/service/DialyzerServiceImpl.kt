package org.elixir_lang.dialyzer.service

import com.intellij.execution.configurations.ParametersList
import com.intellij.execution.process.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.Key
import org.elixir_lang.Mix
import org.elixir_lang.run.ensureWorkingDirectory
import org.elixir_lang.sdk.elixir.Type.Companion.mostSpecificSdk

data class DialyzerWarn(val fileName: String, val line: Int, val message: String = "", val successfulTyping: String = "")
class DialyzerException(message: String?, inner: Throwable?) : Exception(message, inner)

@Service
@com.intellij.openapi.components.State(name = "Dialyzer", storages = [Storage(value = "elixir_dialyzer.xml")])
class DialyzerServiceImpl : DialyzerService {
    override var mixArguments = "dialyzer"
    override var elixirArguments = ""
    override var erlArguments = ""

    private val log = Logger.getInstance(DialyzerServiceImpl::class.java)

    override fun dialyzerWarnings(module: Module): List<DialyzerWarn> {
        val workingDirectory = ensureWorkingDirectory(module)

        val sdk = mostSpecificSdk(module)

        return if (sdk != null) {
            dialyzerWarnings(workingDirectory, sdk)
        } else {
            emptyList()
        }
    }

    private fun dialyzerWarnings(workingDirectory: String, elixirSdk: Sdk) : List<DialyzerWarn> = try {
        parseDialyzerOutput(run(workingDirectory, elixirSdk))
    }
    catch (ex: Exception) {
        throw DialyzerException("Error while running Dialyzer: ${ex.message}", ex)
    }

    private fun run(workingDirectory: String, elixirSdk: Sdk) : Pair<String, String> {
        log.info("Dialyzer starting...")
        val erlArgumentList = ParametersList.parse(erlArguments).toList()
        val elixirArgumentList = ParametersList.parse(elixirArguments).toList()
        val mixArgumentList = ParametersList.parse(mixArguments).toList()
        val commandLine = Mix.commandLine(emptyMap(), workingDirectory, elixirSdk, erlArgumentList, elixirArgumentList)
        commandLine.addParameters(mixArgumentList)

        val processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine)
        val stderrBuilder = StringBuilder()
        val stdoutBuilder = StringBuilder()

        processHandler.addProcessListener(object : ProcessListener {
            override fun startNotified(event: ProcessEvent) {}

            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                when (outputType) {
                    is ProcessOutputType -> when {
                        outputType.isStderr -> {
                            stderrBuilder.append(event.text)
                        }
                        outputType.isStdout -> {
                            stdoutBuilder.append(event.text)
                        }
                        outputType != ProcessOutputType.SYSTEM -> TODO()
                    }
                    else -> TODO()
                }
            }

            override fun processTerminated(event: ProcessEvent) {}
        })

        processHandler.startNotify()
        processHandler.waitFor()

        val stdout = stdoutBuilder.toString()
        val stderr = stderrBuilder.toString()
        log.info("Dialyzer stderr:\n$stderr\nDialyzer stdout:\n$stdout")

        return Pair(stdout, stderr)
    }

    override fun getState(): State = State(mixArguments, elixirArguments, erlArguments)

    override fun loadState(state: State) {
        mixArguments = state.mixArguments
        elixirArguments = state.elixirArguments
        erlArguments = state.erlArguments
    }
}

