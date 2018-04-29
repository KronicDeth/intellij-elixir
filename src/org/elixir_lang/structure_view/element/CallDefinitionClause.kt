package org.elixir_lang.structure_view.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.NameArityRange
import org.elixir_lang.Visibility
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.find_usages.Provider
import org.elixir_lang.navigation.item_presentation.NameArity
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.psi.impl.enclosingMacroCall
import org.elixir_lang.structure_view.element.modular.*
import org.jetbrains.annotations.Contract
import java.util.*

/**
 * Constructs a clause for `callDefinition`.
 *
 * @param callDefinition holds all sibling clauses for `call` for the same name, arity. and time
 * @param call           a def(macro)?p? call
 */
class CallDefinitionClause(private val callDefinition: CallDefinition, call: Call) :
        Element<Call>(call), Presentable, Visible {
    private val visibility: Visibility = visibility(call)!!

    /*
     * Public Instance Methods
     */

    override fun getChildren(): Array<TreeElement> =
        navigationItem.macroChildCalls().let {
            childCallTreeElements(it)
        } ?: emptyArray()

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): ItemPresentation =
            org.elixir_lang.navigation.item_presentation.CallDefinitionHead(
                    callDefinition.presentation as NameArity,
                    visibility(),
                    head(navigationItem)!!
            )

    /**
     * The visibility of the element.
     *
     * @return `Visible.Visibility.PUBLIC` for public call definitions (`def` and `defmacro`);
     * `Visible.Visibility.PRIVATE` for private call definitions (`defp` and `defmacrop`).
     */
    override fun visibility(): Visibility = visibility

    private fun addChildCall(treeElementList: MutableList<TreeElement>, childCall: Call) {
        when {
            Implementation.`is`(childCall) -> Implementation(callDefinition.modular, childCall)
            Module.`is`(childCall) -> Module(callDefinition.modular, childCall)
            Quote.`is`(childCall) -> Quote(this, childCall)
            else -> null
        }?.run {
            treeElementList.add(this)
        }
    }

    @Contract(pure = true)
    private fun childCallTreeElements(childCalls: Array<Call>?): Array<TreeElement>? =
        childCalls?.let {
            val treeElementList = ArrayList<TreeElement>(it.size)

            for (childCall in it) {
                addChildCall(treeElementList, childCall)
            }

            treeElementList.toTypedArray()
        }

    /**
     * Whether this clause would match the given arguments and be called.
     *
     * @param arguments argument being passed to this clauses' function.
     * @return `true` if arguments matches up-to the available information about the arguments; otherwise,
     * `false`
     */
    fun isMatch(arguments: Array<PsiElement>): Boolean = false

    companion object {
        /**
         * Description of element used in [Provider].
         *
         * @param call a [Call] that has already been checked with [.is]
         * @param location where the description will be used
         * @return
         */
        fun elementDescription(call: Call, location: ElementDescriptionLocation): String? =
                when {
                    isFunction(call) -> functionElementDescription(call, location)
                    isMacro(call) -> macroElementDescription(call, location)
                    else -> null
                }

        /**
         * The module or `quote` that encapsulates `call`
         *
         * @param call a def(macro)?p?
         * @return `null` if gets to the enclosing file without finding a quote or module
         */
        @Contract(pure = true)
        fun enclosingModular(call: Call): Modular? =
                enclosingModularMacroCall(call)?.let {
                    modular(it)
                }

        /**
         * The enclosing macro call that acts as the modular scope of `call`.  Ignores enclosing `for` calls that
         * [org.elixir_lang.psi.impl.PsiElementImplKt.enclosingMacroCall] doesn't.
         *
         * @param call a def(macro)?p?
         */
        @JvmStatic
        fun enclosingModularMacroCall(call: Call): Call? {
            var enclosedCall = call
            var enclosingMacroCall: Call?

            while (true) {
                enclosingMacroCall = enclosedCall.enclosingMacroCall()

                if (enclosingMacroCall != null && (enclosingMacroCall.isCalling(KERNEL, ALIAS) || enclosingMacroCall.isCallingMacro(KERNEL, FOR, 2))) {
                    enclosedCall = enclosingMacroCall
                } else {
                    break
                }
            }

            return enclosingMacroCall
        }

        @Contract(pure = true)
        fun modular(enclosingMacroCall: Call): Modular? {
            var modular: Modular? = null

            // All classes under {@link org.elixir_lang.structure_view.element.Modular}
            if (Implementation.`is`(enclosingMacroCall)) {
                val grandScope = enclosingModular(enclosingMacroCall)
                modular = Implementation(grandScope, enclosingMacroCall)
            } else if (Module.`is`(enclosingMacroCall)) {
                val grandScope = enclosingModular(enclosingMacroCall)
                modular = Module(grandScope, enclosingMacroCall)
            } else if (Protocol.`is`(enclosingMacroCall)) {
                val grandScope = enclosingModular(enclosingMacroCall)
                modular = Protocol(grandScope, enclosingMacroCall)
            } else if (Quote.`is`(enclosingMacroCall)) {
                val quoteEnclosingMacroCall = enclosingMacroCall.enclosingMacroCall()
                var quote: Quote? = null

                if (quoteEnclosingMacroCall == null) {
                    quote = Quote(enclosingMacroCall)
                } else if (CallDefinitionClause.`is`(quoteEnclosingMacroCall)) {
                    val callDefinitionClause = CallDefinitionClause.fromCall(quoteEnclosingMacroCall)

                    if (callDefinitionClause == null) {
                        Logger.error(
                                CallDefinitionClause::class.java,
                                "Cannot construct CallDefinitionClause from quote's enclosing macro call",
                                quoteEnclosingMacroCall
                        )
                    } else {
                        quote = Quote(
                                callDefinitionClause,
                                enclosingMacroCall
                        )
                    }
                } else {
                    quote = Quote(modular(quoteEnclosingMacroCall), enclosingMacroCall)
                }

                if (quote != null) {
                    modular = quote.modular()
                }
            } else if (Unknown.`is`(enclosingMacroCall)) {
                val grandScope = enclosingModular(enclosingMacroCall)
                modular = Unknown(grandScope, enclosingMacroCall)
            }

            return modular
        }

        /**
         * The head of the call definition.
         *
         * @param call a call that [.is].
         * @return element for `name(arg, ...) when ...` in `def* name(arg, ...) when ...`
         */
        fun head(call: Call): PsiElement? = call.primaryArguments()?.firstOrNull()

        fun `is`(call: Call): Boolean = isFunction(call) || isMacro(call)
        fun isFunction(call: Call): Boolean = isPrivateFunction(call) || isPublicFunction(call)
        fun isMacro(call: Call): Boolean = isPrivateMacro(call) || isPublicMacro(call)
        fun isPrivateFunction(call: Call): Boolean = isCallingKernelMacroOrHead(call, DEFP)
        fun isPrivateMacro(call: Call): Boolean = isCallingKernelMacroOrHead(call, DEFMACROP)
        fun isPublicFunction(call: Call): Boolean = isCallingKernelMacroOrHead(call, DEF)
        fun isPublicMacro(call: Call): Boolean = isCallingKernelMacroOrHead(call, DEFMACRO)
        fun nameIdentifier(call: Call): PsiElement? = head(call)?.let { CallDefinitionHead.nameIdentifier(it) }

        /**
         * The name and arity range of the call definition this clause belongs to.
         *
         * @param call
         * @return The name and arities of the [CallDefinition] this clause belongs.  Multiple arities occur when
         * default arguments are used, which produces an arity for each default argument that is turned on and off.
         * @see Call.resolvedFinalArityRange
         */
        @JvmStatic
        fun nameArityRange(call: Call): NameArityRange? = head(call)?.let { CallDefinitionHead.nameArityRange(it) }

        /**
         * Whether the `call` is defining something for runtime, like a function, or something for compile time, like
         * a macro.
         *
         * @param call def(macro)?p?
         * @return [Timed.Time.COMPILE] for `defmacrop?`; [Timed.Time.RUN] for `defp?`
         */
        fun time(call: Call): Timed.Time =
                when {
                    isFunction(call) -> Timed.Time.RUN
                    isMacro(call) -> Timed.Time.COMPILE
                    else -> TODO("Should not happen")
                }

        /**
         * @param call
         * @return `Visible.Visibility.PUBLIC` for `def` or `defmacro`; `Visible.Visibility.PRIVATE`
         * for `defp` and `defmacrop`; `null` only if `call` is unrecognized
         */
        fun visibility(call: Call): Visibility? =
                if (isPublicFunction(call) || isPublicMacro(call)) {
                    Visibility.PUBLIC
                } else if (isPrivateFunction(call) || isPrivateMacro(call)) {
                    Visibility.PRIVATE
                } else {
                    null
                }

        private fun functionElementDescription(
                @Suppress("UNUSED_PARAMETER") call: Call,
                location: ElementDescriptionLocation
        ): String? =
                if (location === UsageViewTypeLocation.INSTANCE) {
                    "function"
                } else {
                    null
                }

        private fun isCallingKernelMacroOrHead(call: Call, resolvedName: String): Boolean =
                call.isCallingMacro(KERNEL, resolvedName, 2) ||
                        call.isCalling(KERNEL, resolvedName, 1)

        private fun macroElementDescription(call: Call, location: ElementDescriptionLocation): String? =
                if (location === UsageViewTypeLocation.INSTANCE) {
                    "macro"
                } else {
                    null
                }

        /**
         * Constructs [.callDefinition] from `code`, such as when showing structure in Go To Symbol
         *
         * @param call a def(macro)?p? call
         */
        fun fromCall(call: Call): CallDefinitionClause? =
                CallDefinition.fromCall(call)?.let {
                    CallDefinitionClause(it, call)
                }
    }
}

