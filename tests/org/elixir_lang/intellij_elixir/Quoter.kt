package org.elixir_lang.intellij_elixir

import com.ericsson.otp.erlang.*
import com.intellij.psi.PsiFile
import org.apache.commons.lang.CharUtils
import org.elixir_lang.GenericServer.call
import org.elixir_lang.IntellijElixir
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.impl.ParentImpl.elixirString
import org.hamcrest.CoreMatchers
import org.jetbrains.annotations.Contract
import org.junit.Assert
import org.junit.ComparisonFailure
import java.io.IOException

/**
 * Created by luke.imhoff on 12/31/14.
 */
object Quoter {
    /* remote name is Elixir.IntellijElixir.Quoter because all aliases in Elixir look like atoms prefixed with
       with Elixir. from erlang's perspective. */
    private const val REMOTE_NAME = "Elixir.IntellijElixir.Quoter"
    private const val TIMEOUT_IN_MILLISECONDS = 1000
    @JvmStatic
    fun assertError(file: PsiFile) {
        val text = file.text
        try {
            val quotedMessage = quote(text)
            assertMessageReceived(quotedMessage)
            val status = quotedMessage!!.elementAt(0) as OtpErlangAtom
            val statusString = status.atomValue()
            Assert.assertEquals(statusString, "error")
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: OtpErlangDecodeException) {
            throw RuntimeException(e)
        } catch (e: OtpErlangExit) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun assertExit(file: PsiFile) {
        val text = file.text
        var exception: Any? = null
        try {
            quote(text)
        } catch (e: IOException) {
            exception = e
        } catch (e: OtpErlangDecodeException) {
            exception = e
        } catch (e: OtpErlangExit) {
            exception = e
        }
        Assert.assertThat(exception, CoreMatchers.instanceOf(OtpErlangExit::class.java))
    }

    @Contract("null -> fail")
    private fun assertMessageReceived(message: OtpErlangObject?) {
        Assert.assertNotNull(
                "did not receive message from $REMOTE_NAME@${IntellijElixir.REMOTE_NODE}.  Make sure it is running",
                message
        )
    }

    @JvmStatic
    fun assertQuotedCorrectly(file: PsiFile) {
        val text = file.text

        try {
            val quotedMessage = quote(text)
            assertMessageReceived(quotedMessage)
            val status = quotedMessage!!.elementAt(0) as OtpErlangAtom
            val statusString = status.atomValue()
            val expectedQuoted = quotedMessage.elementAt(1)

            if (statusString == "ok") {
                val actualQuoted = ElixirPsiImplUtil.quote(file)
                assertQuotedCorrectly(expectedQuoted, actualQuoted)
            } else if (statusString == "error") {
                val error = expectedQuoted as OtpErlangTuple
                val line = error.elementAt(0) as OtpErlangLong
                val messageBinary = error.elementAt(1) as OtpErlangBinary
                val message = ElixirPsiImplUtil.javaString(messageBinary)
                val tokenBinary = error.elementAt(2) as OtpErlangBinary
                val token = ElixirPsiImplUtil.javaString(tokenBinary)
                throw AssertionError(
                        "intellij_elixir returned \"$message\" on line $line due to $token, use assertQuotesAroundError if error is expect in Elixir natively, but not in intellij-elixir plugin"
                )
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: OtpErlangDecodeException) {
            throw RuntimeException(e)
        } catch (e: OtpErlangExit) {
            throw RuntimeException(e)
        }
    }

    private fun assertQuotedCorrectly(expectedQuoted: OtpErlangObject,
                                      actualQuoted: OtpErlangObject) {
        if (expectedQuoted != actualQuoted) {
            throw ComparisonFailure(null, toString(expectedQuoted), toString(actualQuoted))
        }
    }

    fun quote(code: String): OtpErlangTuple? {
        val otpNode = IntellijElixir.getLocalNode()
        val otpMbox = otpNode.createMbox()
        val request: OtpErlangObject = elixirString(code)
        return call(
                otpMbox,
                otpNode,
                REMOTE_NAME,
                IntellijElixir.REMOTE_NODE,
                request,
                TIMEOUT_IN_MILLISECONDS
        ) as OtpErlangTuple?
    }

    private fun toString(quoted: OtpErlangBitstr): String =
        quoted.binaryValue().joinToString(prefix = "\"", postfix = "\"") {
            when {
                it.toInt() == 0x0A -> {
                    "\\n"
                }
                CharUtils.isAsciiPrintable(it.toChar()) -> {
                    it.toChar().toString()
                }
                else -> {
                   String.format("\\x%02X", it)
                }
            }
        }

    private fun toString(quoted: OtpErlangList): String =
        quoted.elements().joinToString(prefix = "[", postfix = "]") { toString(it) }

    private fun toString(quoted: OtpErlangObject): String = if (quoted is OtpErlangBoolean ||
            quoted is OtpErlangAtom ||
            quoted is OtpErlangByte ||
            quoted is OtpErlangChar ||
            quoted is OtpErlangFloat ||
            quoted is OtpErlangDouble ||
            quoted is OtpErlangExternalFun ||
            quoted is OtpErlangFun ||
            quoted is OtpErlangInt ||
            quoted is OtpErlangLong ||
            quoted is OtpErlangMap ||
            quoted is OtpErlangPid ||
            quoted is OtpErlangString) {
        quoted.toString()
    } else if (quoted is OtpErlangBitstr) {
        toString(quoted)
    } else if (quoted is OtpErlangList) {
        toString(quoted)
    } else if (quoted is OtpErlangTuple) {
        toString(quoted)
    } else {
        throw IllegalArgumentException("Don't know how to convert ${quoted.javaClass} to string")
    }

    private fun toString(quoted: OtpErlangTuple): String =
        quoted.elements().joinToString(prefix = "{", postfix = "}") { toString(it) }
}
