package org.elixir_lang.facet.sdks

import com.intellij.openapi.projectRoots.Sdk
import com.intellij.ui.CollectionListModel
import com.intellij.ui.components.JBList
import org.elixir_lang.facet.sdk.Name
import javax.swing.ListSelectionModel

class List(private val configurable: Configurable): JBList<Sdk>() {
    init {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellRenderer = Name()
    }

    fun refresh() {
        model = CollectionListModel<Sdk>(configurable.sdksService.elixirSdkList())
    }
}
