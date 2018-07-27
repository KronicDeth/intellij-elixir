package org.elixir_lang

import com.ericsson.otp.erlang.OtpMbox
import com.ericsson.otp.erlang.OtpNode
import com.intellij.util.TimeoutUtil.sleep
import java.io.IOException

/**
 * `{atom(), node()}` `t:GenServer.server/0` that works with JInterface, so both the `atom()` and `node()` are
 * [String]].
 */
data class Server(val registeredNamed: String, val nodeName: String) {
    /**
     * @param ensureAllStarted ensures that `epmd -d` and the [remoteNodeName] and [remoteRegisteredName] is started.
     */
    fun mailBox(remote: Server, cookie: String, ensureAllStarted: () -> Unit): MailBox  {
        val localNode = localNode(remote, cookie, ensureAllStarted)
        val localRegisteredMbox = mbox(localNode)

        return MailBox(localNode, localRegisteredMbox).apply {
            waitFor(remote)
        }
    }

    override fun toString(): String = "{:$registeredNamed, :\"$nodeName\"}"

    /**
     * @param ensureAllStarted ensures that `epmd -d` and the [remoteNodeName] and [remoteRegisteredName] is started.
     */
    private fun localNode(remote: Server, cookie: String, ensureAllStarted: () -> Unit): OtpNode {
        /* ensure `epmd` is started or `OtpNode()` will fail
            `Nameserver not responding on HOST when publishing <debuggerName>` */
        ensureAllStarted()

        return waitForNameServer(nodeName, cookie).apply {
            waitFor(remote)
        }
    }

    private fun mbox(localNode: OtpNode): OtpMbox = localNode.createMbox(registeredNamed)
}

@Throws(IOException::class)
private fun waitForNameServer(nodeName: String, cookie: String): OtpNode {
    var totalTime = 0L
    val timeout = 2000L
    var otpNode: OtpNode

    while (true) {
        try {
            otpNode = OtpNode(nodeName, cookie)
            break
        } catch (ioException: IOException) {
            sleep(timeout)
            totalTime += timeout

            if (totalTime >= NAME_SERVER_TIMEOUT) {
                throw ioException
            }
        }
    }

    return otpNode
}

private fun OtpNode.waitFor(remote: Server) = waitForNode(remote.nodeName)

private fun OtpNode.waitForNode(remoteNodeName: String) {
    while (!ping(remoteNodeName, 100)) {}
}

private const val NAME_SERVER_TIMEOUT = 10000
