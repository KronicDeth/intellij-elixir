package org.elixir_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.stub.index.ModularName
import org.intellij.erlang.psi.ErlangAtom

class Module : Annotator {
    override fun annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder) {
        if (psiElement is ErlangAtom) {
            val name = psiElement.name
            if (name.startsWith(ELIXIR_ALIAS_PREFIX)) {
                val project = psiElement.getProject()
                val namedElementCollection = StubIndex.getElements(
                        ModularName.KEY,
                        name,
                        project,
                        GlobalSearchScope.allScope(project),
                        NamedElement::class.java
                )
                if (namedElementCollection.size > 0) {
                    val unprefixedName = name.substring(ELIXIR_ALIAS_PREFIX.length)
                    annotationHolder
                            .newAnnotation(HighlightSeverity.WEAK_WARNING, "Resolves to Elixir Module $unprefixedName")
                            .textAttributes(DefaultLanguageHighlighterColors.LINE_COMMENT)
                            .create()
                } else {
                    annotationHolder
                            .newAnnotation(HighlightSeverity.ERROR, "Unresolved Elixir Module")
                            .create()
                }
            }
        }
    }

    companion object {
        const val ELIXIR_ALIAS_PREFIX = "Elixir."
    }
}
