package org.elixir_lang.beam.chunk

import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.debugger.ElixirXValuePresentation
import javax.swing.table.DefaultTableCellRenderer

fun inspect(term: OtpErlangObject): String =
    XValueRenderer().let { renderer ->
        ElixirXValuePresentation(term).renderValue(renderer)
        renderer.getText()
    }

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
