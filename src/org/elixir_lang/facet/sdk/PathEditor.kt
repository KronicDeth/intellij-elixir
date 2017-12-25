package org.elixir_lang.facet.sdk

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.ProjectBundle
import com.intellij.openapi.projectRoots.ui.SdkPathEditor
import com.intellij.openapi.roots.OrderRootType


private val DISPLAY_NAME_BY_ORDER_ROOT_TYPE = mapOf<OrderRootType, String>(
        // https://github.com/JetBrains/intellij-community/blob/da94359b1054070cb7d7280673a773daf87cc7d8/java/idea-ui/src/com/intellij/openapi/roots/ui/configuration/libraryEditor/ClassesOrderRootTypeUIFactory.java#L53
        OrderRootType.CLASSES to ProjectBundle.message("sdk.configure.classpath.tab"),
        // https://github.com/JetBrains/intellij-community/blob/da94359b1054070cb7d7280673a773daf87cc7d8/java/idea-ui/src/com/intellij/openapi/roots/ui/configuration/libraryEditor/SourcesOrderRootTypeUIFactory.java#L57
        OrderRootType.SOURCES to ProjectBundle.message("sdk.configure.sourcepath.tab")
)

fun pathEditor(orderRootType: OrderRootType): SdkPathEditor? =
        DISPLAY_NAME_BY_ORDER_ROOT_TYPE[orderRootType]?.let { displayName ->
            SdkPathEditor(
                    displayName,
                    orderRootType,
                    FileChooserDescriptor(true, true, false, false, false, true)
            )
        }
