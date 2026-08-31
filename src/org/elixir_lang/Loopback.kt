package org.elixir_lang

import com.ericsson.otp.erlang.OtpServerTransport
import com.ericsson.otp.erlang.OtpSocketTransport
import com.ericsson.otp.erlang.OtpSocketTransportFactory
import com.ericsson.otp.erlang.OtpTransport
import com.ericsson.otp.erlang.OtpTransportFactory
import java.net.InetAddress
import java.net.ServerSocket

/**
 * Binds a JInterface node's distribution listener to loopback rather than to every interface.
 *
 * The quoter daemon this talks to is always on this machine, but JInterface binds its listener to
 * [ServerSocket]'s all-interfaces default - the IPv6 wildcard, and a listener distinct from the one the
 * quoter's `erl.exe` opens, which is why Windows Firewall prompts for `java.exe` too. Only the listener
 * is narrowed; outbound connections go through JInterface's own transport.
 */
object LoopbackTransportFactory : OtpTransportFactory {
    private val delegate = OtpSocketTransportFactory()

    // Nullable deliberately: `OtpEpmd.r4_publish` registers the node by calling this with an explicit
    // `(String) null` for "localhost". Tightening the platform type compiles, then throws on every node.
    override fun createTransport(address: String?, port: Int): OtpTransport =
        delegate.createTransport(address, port)

    override fun createTransport(address: InetAddress?, port: Int): OtpTransport =
        delegate.createTransport(address, port)

    override fun createServerTransport(port: Int): OtpServerTransport = LoopbackServerTransport(port)
}

/**
 * [com.ericsson.otp.erlang.OtpServerSocketTransport] with the bind address pinned, and otherwise
 * identical to it - including the `TCP_NODELAY` its `accept` sets.
 */
private class LoopbackServerTransport(port: Int) : OtpServerTransport {
    // Backlog 0 asks for ServerSocket's own default, as the (port) constructor would; only the address
    // differs.
    private val serverSocket = ServerSocket(port, 0, InetAddress.getLoopbackAddress())

    override fun getLocalPort(): Int = serverSocket.localPort

    override fun accept(): OtpTransport =
        serverSocket.accept().also { it.tcpNoDelay = true }.let(::OtpSocketTransport)

    override fun close() = serverSocket.close()
}
