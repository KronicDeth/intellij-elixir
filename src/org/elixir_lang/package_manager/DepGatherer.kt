package org.elixir_lang.package_manager

import com.intellij.psi.PsiElementVisitor
import org.elixir_lang.mix.Dep

abstract class DepGatherer : PsiElementVisitor() {
    val depSet: MutableSet<Dep> = mutableSetOf()
}
