package org.elixir_lang.facet

import com.intellij.facet.FacetConfiguration
import com.intellij.facet.ui.FacetEditorContext
import com.intellij.facet.ui.FacetEditorTab
import com.intellij.facet.ui.FacetValidatorsManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import com.intellij.openapi.util.text.StringUtil
import org.elixir_lang.sdk.elixir.Type
import org.jdom.Element

class Configuration : FacetConfiguration {
    var sdk: Sdk? = null

    override fun createEditorTabs(editorContext: FacetEditorContext,
                                  validatorsManager: FacetValidatorsManager): Array<FacetEditorTab> = arrayOf()

    @Throws(InvalidDataException::class)
    override fun readExternal(element: Element) {
        val sdkName = element.getAttributeValue(SDK_NAME)

        sdk = if (StringUtil.isEmpty(sdkName)) {
            null
        } else {
            ProjectJdkTable.getInstance().findJdk(sdkName, Type.getInstance().name)
        }

        sdk?.let {
            ApplicationManager
                    .getApplication().let { application ->
                        application.invokeLater {
                            application.runWriteAction {
                                application
                                        .messageBus
                                        .syncPublisher(ProjectJdkTable.JDK_TABLE_TOPIC)
                                        .jdkAdded(it)
                            }
                        }
                    }
        }
    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(element: Element) {
        element.setAttribute(SDK_NAME, sdk?.name ?: "")
    }

    companion object {
        private val SDK_NAME = "sdkName"
    }
}
