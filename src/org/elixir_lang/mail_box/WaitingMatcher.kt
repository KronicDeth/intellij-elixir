package org.elixir_lang.mail_box

import com.ericsson.otp.erlang.OtpErlangObject
import java.util.concurrent.CompletableFuture

class WaitingMatcher(val matcher: (message: OtpErlangObject) -> Any?) : CompletableFuture<Any>()
