package org.elixir_lang.psi.impl

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirAlias
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.QualifiedAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.UNQUOTE
import org.elixir_lang.structure_view.element.modular.Implementation
import org.jetbrains.annotations.Contract

object PsiNamedElementImpl {
    @JvmStatic
    fun getName(alias: ElixirAlias): String {
        return alias.text
    }

    @JvmStatic
    fun getName(qualifiedAlias: QualifiedAlias): String {
        return qualifiedAlias.text
    }

    @Contract(pure = true)
    @JvmStatic
    fun getName(namedElement: NamedElement): String? {
        val nameIdentifier = namedElement.nameIdentifier
        var name: String? = null

        if (nameIdentifier != null) {
            name = unquoteName(namedElement, nameIdentifier.text)
        } else {
            if (namedElement is Call) {
                val call = namedElement as Call

                /* The name of the module defined by {@code defimpl PROTOCOL[ for: MODULE]} is derived by combining the
                   PROTOCOL and MODULE name into PROTOCOL.MODULE.  Neither piece is really the "name" or
                   "nameIdentifier" element of the implementation because changing the PROTOCOL make the implementation
                   just for that different Protocol and changing the MODULE makes the implementation for a different
                   MODULE.  If `for:` isn't given, it's really the enclosing {@code defmodule MODULE} whose name should
                   be changed. */
                if (Implementation.`is`(call)) {
                    name = Implementation.name(call)
                }
            }
        }

        return name
    }

    /**
     * If `name` is `"unquote"` then the [Call.primaryArguments] single argument is added to the
     * name.
     */
    @JvmStatic
    fun unquoteName(named: PsiElement, name: String?): String? {
        var unquotedName = name

        if (named is Call && UNQUOTE == name) {
            val primaryArguments = named.primaryArguments()

            if (primaryArguments != null && primaryArguments.size == 1) {
                unquotedName += "(" + primaryArguments[0].text + ")"
            }
        }

        return unquotedName
    }
}
