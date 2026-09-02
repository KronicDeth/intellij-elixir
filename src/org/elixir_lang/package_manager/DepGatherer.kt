package org.elixir_lang.package_manager

import com.intellij.psi.PsiElementVisitor
import org.elixir_lang.mix.Dep

abstract class DepGatherer : PsiElementVisitor() {
    val depSet: MutableSet<Dep> = mutableSetOf()

    /**
     * The dependency directory the package file names, verbatim and relative to the file, or `null`
     * when it names none and the package manager's default applies.
     *
     * Open with a `null` default so a package manager that has no such notion - or that lives
     * outside this repository, like the rebar3 one registered by the Erlang plugin - keeps working
     * without overriding it.
     */
    open val depsPath: String? = null
}
