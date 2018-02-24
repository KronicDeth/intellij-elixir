package org.elixir_lang.debugger.stack_frame.value

import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.icons.AllIcons
import javax.swing.Icon

open class ArrayBase<T : OtpErlangObject>(value: T, childrenCount: Int) : Base<T>(value, childrenCount) {
    override fun getIcon(): Icon = AllIcons.Debugger.Db_array
}
