package org.elixir_lang.facet.sdk

import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.ui.CollectionListModel
import org.elixir_lang.facet.SdksService
import org.elixir_lang.sdk.elixir.Type
import javax.swing.ComboBoxModel

class Model:
        CollectionListModel<Sdk?>(
                SdksService.getInstance()?.projectJdkImplList(Type::class.java) ?: mutableListOf(),
                false
        ),
        ComboBoxModel<Sdk?> {
    override fun getElementIndex(item: Sdk?): Int {
        return if (item != null) {
            internalList.indexOfFirst { it?.name == item.name }
        } else {
            0
        }
    }

    init {
        internalList.add(0, null)
        SdksService.getInstance()?.apply {
            getModel().addListener(object : SdkModel.Listener {
                override fun beforeSdkRemove(sdk: Sdk?) {
                    sdk?.let {
                        remove(it)
                    }
                }

                override fun sdkAdded(sdk: Sdk?) {
                    sdk?.let {
                        if (it.sdkType is Type) {
                            add(sdk)
                        }
                    }
                }
                override fun sdkChanged(sdk: Sdk?, previousName: String?) {
                    val previousIndex = internalList.indexOfFirst { indexSdk ->
                        indexSdk?.name == previousName
                    }

                    if (previousIndex > 0) {
                        fireContentsChanged(this@Model, previousIndex, previousIndex)
                    }
                }

                override fun sdkHomeSelected(sdk: Sdk?, newSdkHome: String?) {}
            })
        }
    }

    private var _selectedItem: Sdk? = items[0]

    override fun getSelectedItem(): Any? {
        return _selectedItem
    }

    override fun setSelectedItem(item: Any?) {
        if (_selectedItem !== item) {
            _selectedItem = item as Sdk?
            update()
        }
    }

    private fun update() {
        fireContentsChanged(this, -1, -1)
    }
}
