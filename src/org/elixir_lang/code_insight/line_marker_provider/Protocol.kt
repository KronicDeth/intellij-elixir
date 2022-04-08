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
import com.intellij.psi.*
import org.elixir_lang.Icons
import org.elixir_lang.psi.ElixirAccessExpression
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.Protocol
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.semantic
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.Icon

class Protocol : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? =
            when (element) {
                is ElixirAccessExpression -> null
                else -> element.semantic?.let { semantic ->
                    when (semantic) {
                        is Protocol -> {
                            val targets: NotNullLazyValue<Collection<PsiElement>> = NotNullLazyValue.createValue {
                                semantic.implementations.map(org.elixir_lang.semantic.Implementation::psiElement)
                            }

                            ImplsGutterIconBuilder()
                                    .setTargets(targets)
                                    .createLineMarkerInfo(element)
                        }
                        is Clause -> {
                            semantic.nameArityInterval?.let { protocolNameArityInterval ->
                                val protocolName = protocolNameArityInterval.name
                                val protocolArityInterval = protocolNameArityInterval.arityInterval

                                semantic
                                        .definition
                                        .enclosingModular.let { it as? Protocol }?.let { protocol ->
                                            val targets: NotNullLazyValue<Collection<PsiElement>> = NotNullLazyValue.createValue {
                                                protocol
                                                        .implementations
                                                        .flatMap(org.elixir_lang.semantic.Implementation::exportedCallDefinitions).filter { implementationCallDefinitionClause ->
                                                            implementationCallDefinitionClause.nameArityInterval?.let { implementationNameArityInterval ->
                                                                implementationNameArityInterval.name == protocolName && implementationNameArityInterval.arityInterval.overlaps(protocolArityInterval)
                                                            } == true
                                                        }
                                                        .flatMap(Definition::clauses)
                                                        .map(Clause::psiElement)
                                            }

                                            ImplsGutterIconBuilder()
                                                    .setTargets(targets)
                                                    .createLineMarkerInfo(element)
                                        }
                            }
                        }
                        else -> null
                    }
                }
            }

    private class ImplsGutterIconBuilder :
            NavigationGutterIconBuilder<PsiElement>(
                    Icons.Protocol.GoToImplementations,
                    DEFAULT_PSI_CONVERTOR,
                    PSI_GOTO_RELATED_ITEM_PROVIDER
            ) {

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
