package org.elixir_lang.psi.impl

import com.intellij.openapi.editor.Document
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import org.elixir_lang.psi.ElixirAccessExpression
import org.elixir_lang.psi.ElixirList
import org.elixir_lang.psi.ElixirStabBody
import org.elixir_lang.psi.call.Call
import java.util.*

fun PsiElement.document(): Document? = containingFile.viewProvider.document

fun PsiElement.macroChildCallList(): MutableList<Call> {
    val callList: MutableList<Call>

    if (this is ElixirAccessExpression) {
        callList = this@macroChildCallList.getFirstChild().macroChildCallList()
    } else if (this is ElixirList || this is ElixirStabBody) {
        callList = ArrayList()

        var child: PsiElement? = firstChild
        while (child != null) {
            if (child is Call) {
                callList.add(child)
            } else if (child is ElixirAccessExpression) {
                callList.addAll(child.macroChildCallList())
            }
            child = child.nextSibling
        }
    } else {
        callList = mutableListOf()
    }

    return callList
}

fun PsiElement.moduleWithDependentsScope(): GlobalSearchScope {
    val virtualFile = containingFile.virtualFile
    val project = project
    val module = ModuleUtilCore.findModuleForFile(
            virtualFile,
            project
    )

    // module can be null for scratch files
    return if (module != null) {
        GlobalSearchScope.moduleWithDependentsScope(module)
    } else {
        GlobalSearchScope.allScope(project)
    }
}
