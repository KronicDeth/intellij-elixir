package org.elixir_lang.dialyzer.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import org.apache.commons.io.IOUtils
import java.io.File
import java.nio.charset.Charset

data class DialyzerWarn(val fileName: String, val line: Int, val message: String = "", val successfulTyping: String = "")
class DialyzerException(message: String?, inner: Throwable?) : Exception(message, inner)

@Service
@com.intellij.openapi.components.State(name = "Dialyzer", storages = [Storage(value = "elixir_dialyzer.xml")])
class DialyzerServiceImpl(private val project: Project) : DialyzerService {

    override var mixDialyzerCommand = "mix dialyzer"
    private val log = Logger.getInstance(DialyzerServiceImpl::class.java)

    override fun getDialyzerWarnings() : List<DialyzerWarn>  {
        return try {
            parseDialyzerOutput(run())
        }
        catch (ex: Exception) {
            throw DialyzerException("Error while running Dialyzer: ${ex.message}", ex)
        }
    }

    private fun run() : Pair<String, String> {
        log.info("Dialyzer starting...")
        val proc = ProcessBuilder(mixDialyzerCommand.split(' '))
                .directory(File(project.basePath!!))
                .start()
        val stderr = IOUtils.toString(proc.errorStream, Charset.defaultCharset())
        val stdout = IOUtils.toString(proc.inputStream, Charset.defaultCharset())
        log.info("Dialyzer stderr:\n$stderr\nDialyzer stdout:\n$stdout")
        proc.destroy()
        return Pair(stdout, stderr)
    }

    override fun getState(): State? {
        return State(mixDialyzerCommand)
    }

    override fun loadState(state: State) {
        mixDialyzerCommand = state.mixDialyzerCommand
    }
}

