package org.elixir_lang.code_insight.line_marker_provider

import com.intellij.codeInsight.CodeInsightBundle
import com.intellij.codeInsight.ContainerProvider
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.codeInsight.navigation.NavigationGutterIconRenderer
import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.NotNullLazyValue
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPsiElementPointer
import org.elixir_lang.Icons.PROTOCOL
import org.elixir_lang.psi.Protocol
import org.elixir_lang.psi.call.Call
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.Icon

class Protocol : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? =
            when (element) {
                is Call -> getLineMarkerInfo(element)
                else -> null
            }

    private fun getLineMarkerInfo(call: Call): LineMarkerInfo<*>? =
            if (Protocol.`is`(call)) {
                val targets: NotNullLazyValue<Collection<PsiElement>> = NotNullLazyValue.createValue {
                    val implementations = mutableListOf<PsiElement>()

                    Protocol.processImplementations(call) { implementation ->
                        implementations.add(implementation)
                    }

                    implementations
                }

                ImplsGutterIconBuilder()
                        .setTargets(targets)
                        .createLineMarkerInfo(call)
            } else {
                null
            }
    private class ImplsGutterIconBuilder() :
            NavigationGutterIconBuilder<PsiElement>(PROTOCOL, DEFAULT_PSI_CONVERTOR, PSI_GOTO_RELATED_ITEM_PROVIDER) {

        override fun createGutterIconRenderer(
                pointers: NotNullLazyValue<List<SmartPsiElementPointer<*>>>,
                renderer: Computable<PsiElementListCellRenderer<*>>,
                empty: Boolean
        ): NavigationGutterIconRenderer {
            return ImplsNavigationGutterIconRenderer(
                    popupTitle = myPopupTitle,
                    emptyText = myEmptyText,
                    pointers = pointers,
                    cellRenderer = renderer,
                    alignment = myAlignment,
                    icon = myIcon,
                    tooltipText = myTooltipText,
                    empty = empty
            )
        }
    }
    private class ImplsNavigationGutterIconRenderer(
            popupTitle: String?,
            emptyText: String?,
            pointers: NotNullLazyValue<List<SmartPsiElementPointer<*>>>,
            cellRenderer: Computable<PsiElementListCellRenderer<*>>,
            private val alignment: Alignment,
            private val icon: Icon,
            private val tooltipText: String?,
            private val empty: Boolean
    ) : NavigationGutterIconRenderer(popupTitle, emptyText, cellRenderer, pointers) {
        override fun isNavigateAction(): Boolean = !empty
        override fun getIcon(): Icon = icon
        override fun getTooltipText(): String? = tooltipText
        override fun getAlignment(): Alignment = alignment

        override fun navigate(event: MouseEvent?, elt: PsiElement?) {
            if (event == null || elt == null) return

            val targets = targetElements.filterIsInstance<NavigatablePsiElement>().toTypedArray()
            val renderer = myCellRenderer.compute()

            Arrays.sort(targets, Comparator.comparing(renderer::getComparingObject))
            val escapedName = escapedName(elt)

            @Suppress("DialogTitleCapitalization")
            PsiElementListNavigator.openTargets(
                    event,
                    targets,
                    CodeInsightBundle.message("goto.implementation.chooserTitle", escapedName, targets.size, ""),
                    CodeInsightBundle.message("goto.implementation.findUsages.title", escapedName, targets.size),
                    renderer
            )
        }

        private fun escapedName(element: PsiElement): String {
            val presentation = (element as NavigationItem).presentation!!
            val containerText = containerText(element)
            val prefix = if (containerText == null) {
                ""
            } else {
                "$containerText."
            }
            val fullName = prefix + presentation.presentableText

            return StringUtil.escapeXmlEntities(fullName)
        }

        private fun containerText(element: PsiElement): String? {
            val container = container(element)
            val containerPresentation = if (container == null || container is PsiFile) null else (container as NavigationItem).presentation
            return containerPresentation?.presentableText
        }

        private fun container(refElement: PsiElement): PsiElement? {
            for (provider in ContainerProvider.EP_NAME.extensions) {
                val container = provider.getContainer(refElement)
                if (container != null) return container
            }
            return refElement.parent
        }

    }
}
