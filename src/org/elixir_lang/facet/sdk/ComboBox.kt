package org.elixir_lang.facet.sdk

import com.intellij.openapi.projectRoots.Sdk
import javax.swing.JComboBox

class ComboBox: JComboBox<Sdk?>(Model()) {
    init {
        renderer = Name()
    }
}
