package org.elixir_lang.beam.chunk

import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.term.inspect
import javax.swing.table.DefaultTableCellRenderer

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
