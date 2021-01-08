package org.elixir_lang.exunit

import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.sm.runner.OutputEventSplitter
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter
import com.intellij.openapi.util.Key
import org.elixir_lang.mix.runner.Status

class MixOutputToGeneralTestEventsConverter internal constructor(testFrameworkName: String, consoleProperties: TestConsoleProperties) : OutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties) {
    private val splitter: OutputEventSplitter = object : OutputEventSplitter() {
        override fun onTextAvailable(text: String, outputType: Key<*>) {
            processConsistentText(text, outputType)
        }
    }
    private var stderrStatus: Status? = null
    private var stdoutStatus: Status? = null

    override fun flushBufferOnProcessTermination(code: Int) {
        super.flushBufferOnProcessTermination(code)
        processStatuses()
    }

    override fun process(text: String, outputType: Key<*>?) {
        splitter.process(text, outputType!!)
    }

    private fun processStatus(status: Status, outputType: Key<*>) {
        for (text in status.toTeamCityMessageList()) {
            super.processConsistentText(text, outputType)
        }
    }

    private fun processStatuses() {
        stderrStatus?.let {
            processStatus(it, ProcessOutputTypes.STDERR)
            stderrStatus = null
        }
        stdoutStatus?.let {
            processStatus(it, ProcessOutputTypes.STDOUT)
            stdoutStatus = null
        }
    }

    public override fun processConsistentText(text: String, outputType: Key<*>) {
        if (outputType === ProcessOutputTypes.STDERR) {
            if (stderrStatus != null) {
                when {
                    text.startsWith("  ") -> {
                        stderrStatus!!.addLine(text)
                    }
                    text == "\n" -> {
                        processStatus(stderrStatus!!, outputType)
                        stderrStatus = null
                    }
                    else -> {
                        super.processConsistentText(text, outputType)
                    }
                }
            } else {
                stderrStatus = Status.fromStderrLine(text)
                if (stderrStatus == null) {
                    super.processConsistentText(text, outputType)
                }
            }
        } else if (outputType === ProcessOutputTypes.STDOUT) {
            if (stdoutStatus != null) {
                if (text == "\n") {
                    processStatus(stdoutStatus!!, outputType)
                    stdoutStatus = null
                } else {
                    stdoutStatus!!.addLine(text)
                }
            } else {
                stdoutStatus = Status.fromStdoutLine(text)
                if (stdoutStatus == null) {
                    super.processConsistentText(text, outputType)
                }
            }
        } else {
            super.processConsistentText(text, outputType)
        }
    }

}
