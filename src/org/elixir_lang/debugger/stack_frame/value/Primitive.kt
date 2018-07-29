package org.elixir_lang.debugger.stack_frame.value

import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.icons.AllIcons
import javax.swing.Icon

open class Primitive<out T : OtpErlangObject>(term: T) : Presentable<T>(term) {
    override val hasChildren: Boolean = false
    override val icon: Icon = AllIcons.Debugger.Db_primitive
}
