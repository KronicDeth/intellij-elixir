package org.elixir_lang.beam.chunk

import com.ericsson.otp.erlang.OtpErlangObject
import com.intellij.xdebugger.impl.ui.tree.nodes.XValuePresentationUtil
import org.elixir_lang.debugger.ElixirXValuePresentation
import javax.swing.table.DefaultTableCellRenderer

fun inspect(term: OtpErlangObject): String =
    XValuePresentationUtil.computeValueText(ElixirXValuePresentation(term))

class OtpErlangObjectTableCellRenderer: DefaultTableCellRenderer() {
    override fun setValue(value: Any?) {
        val stringValue: String = if (value != null) {
            inspect(value as OtpErlangObject)
        } else {
            ""
        }

        super.setValue(stringValue)
    }
}
