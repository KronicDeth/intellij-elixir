package org.elixir_lang.leex.reference.resolver

import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.roots.impl.LibraryScopeCache
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.siblings
import org.elixir_lang.leex.reference.Assign
import org.elixir_lang.psi.*
import org.elixir_lang.psi.Modular.callDefinitionClauseCallFoldWhile
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.StubBased
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.operation.*
import org.elixir_lang.psi.stub.index.AllName

object Assign: ResolveCache.PolyVariantResolver<org.elixir_lang.leex.reference.Assign> {
    override fun resolve(assign: Assign, incompleteCode: Boolean): Array<ResolveResult> =
        assign.element.containingFile.context
                ?.let { it as? Call }
                ?.let { viewModule ->
                    val viewModuleResolveResultList = resolveInViewModule(assign, incompleteCode, viewModule)

                    if (viewModuleResolveResultList.any(ResolveResult::isValidResult)) {
                        viewModuleResolveResultList
                    } else {
                        val liveComponentCallResolveResultList = resolveInLiveComponentCalls(assign, incompleteCode, viewModule)

                        if (liveComponentCallResolveResultList.any(ResolveResult::isValidResult)) {
                            viewModuleResolveResultList + liveComponentCallResolveResultList
                        } else {
                            val phoenixLiveViewResolveResultList = resolveInPhoenixLiveView(assign, incompleteCode)

                            viewModuleResolveResultList + liveComponentCallResolveResultList + phoenixLiveViewResolveResultList
                        }
                    }
                }
                .orEmpty()
                .toTypedArray()

    private fun resolveInViewModule(assign: Assign, incompleteCode: Boolean, viewModule: Call): List<ResolveResult> =
        callDefinitionClauseCallFoldWhile(viewModule, emptyList<ResolveResult>()) { callDefinitionClauseCall, acc ->
            // Always continue because there is no way to determine which definition is called last if the view module has enough helper functions.
            AccumulatorContinue(resolveInCallDefinitionClause(assign, incompleteCode, callDefinitionClauseCall, acc), true)
        }.accumulator

    private fun resolveInCallDefinitionClause(assign: Assign, incompleteCode: Boolean, callDefinitionClause: Call, initial: List<ResolveResult>): List<ResolveResult> =
            resolveInCallDefinitionClause(assign, incompleteCode, callDefinitionClause, initial, ::resolveInCallDefinitionClauseExpression)

    private tailrec fun resolveInCallDefinitionClauseExpression(assign: Assign, incompleteCode: Boolean, expression: PsiElement, initial: List<ResolveResult>): List<ResolveResult> =
            when (expression) {
                is ElixirAccessExpression, is ElixirStabBody, is ElixirTuple -> expression
                        .lastChild
                        .siblings(forward = false, withSelf = true)
                        .filter { it.node is CompositeElement }
                        .fold(initial) { acc, child ->
                            resolveInCallDefinitionClauseExpression(assign, incompleteCode, child, acc)
                        }
                is Arrow -> {
                    val leftAcc = expression.leftOperand()?.let { leftOperand ->
                        resolveInCallDefinitionClauseExpression(assign, incompleteCode, leftOperand, initial)
                    } ?: initial

                    val rightOperand = expression.rightOperand()

                    if (rightOperand != null) {
                        resolveInCallDefinitionClauseExpression(assign, incompleteCode, rightOperand, leftAcc)
                    } else {
                        leftAcc
                    }
                }
                is Match -> {
                    val rightOperand = expression.rightOperand()

                    if (rightOperand != null) {
                        resolveInCallDefinitionClauseExpression(assign, incompleteCode, rightOperand, initial)
                    } else {
                        initial
                    }
                }
                is ElixirAtom, is ElixirAtomKeyword, is ElixirEndOfExpression, is ElixirMapOperation, is ElixirStructOperation, is QuotableKeywordList, is Quote -> initial
                // After `None` as `assign/2` and `assign/3` have options
                // After `Match` because it is a more specific `Call`
                is Call ->
                    when (expression.functionName()) {
                        "assign" ->
                            when (expression.resolvedFinalArity()) {
                                2 -> expression
                                        .finalArguments()
                                        ?.last()
                                        ?.let { resolveInAssign2Argument(assign, incompleteCode, it, initial) }
                                3 -> expression
                                        .finalArguments()
                                        ?.let { finalArguments -> finalArguments[finalArguments.size - 2] }
                                        ?.let { resolveInAtomArgument(assign, incompleteCode, it, initial) }
                                else -> null
                            } ?: initial
                        "assign_new" ->
                            when (expression.resolvedFinalArity()) {
                                3 -> expression
                                        .finalArguments()
                                        ?.let { finalArguments -> finalArguments[finalArguments.size - 2] }
                                        ?.let { resolveInAtomArgument(assign, incompleteCode, it, initial) }
                                else -> null
                            } ?: initial
                        else -> {
                            val stab = expression.doBlock?.stab

                            if (stab != null) {
                                resolveInCallDefinitionClauseExpression(assign, incompleteCode, stab, initial)
                            } else {
                                initial
                            }
                        }
                    }
                is ElixirStab -> {
                    val stabBody = expression.stabBody

                    if (stabBody != null) {
                        resolveInCallDefinitionClauseExpression(assign, incompleteCode, stabBody, initial)
                    } else {
                        expression.stabOperationList.fold(initial) { acc, stabOperation ->
                            resolveInCallDefinitionClauseExpression(assign, incompleteCode, stabOperation, acc)
                        }
                    }
                }
                is ElixirStabOperation -> {
                    val rightOperand = expression.rightOperand()

                    if (rightOperand != null) {
                        resolveInCallDefinitionClauseExpression(assign, incompleteCode, rightOperand, initial)
                    } else {
                        initial
                    }
                }
                else -> {
                    TODO()
                }
            }

    private tailrec fun resolveInAssign2Argument(assign: Assign, incompleteCode: Boolean, expression: PsiElement, initial: List<ResolveResult>): List<ResolveResult> =
        when (expression) {
            is ElixirAccessExpression -> resolveInAssign2Argument(assign, incompleteCode, expression.stripAccessExpression(), initial)
            is ElixirKeywordKey -> {
                if (expression.charListLine == null && expression.stringLine == null) {
                    val resolvedName = expression.text
                    val assignName = assign.name

                    if (resolvedName.startsWith(assignName)) {
                        val validResult = resolvedName == assignName

                        initial + listOf(PsiElementResolveResult(expression, validResult))
                    } else {
                        null
                    }
                } else {
                    null
                } ?: initial
            }
            is ElixirMapOperation -> resolveInAssign2Argument(assign, incompleteCode, expression.mapArguments, initial)
            is ElixirMapArguments -> {
                val child = expression.mapConstructionArguments ?: expression.mapConstructionArguments

                if (child != null) {
                    resolveInAssign2Argument(assign, incompleteCode, child, initial)
                } else {
                    initial
                }
            }
            is ElixirMapConstructionArguments -> expression.arguments().fold(initial) { acc, argument ->
                resolveInAssign2Argument(assign, incompleteCode, argument, acc)
            }
            is QuotableKeywordList -> expression.quotableKeywordPairList().fold(initial) { acc, keywordPair ->
                resolveInAssign2Argument(assign, incompleteCode, keywordPair, acc)
            }
            is QuotableKeywordPair -> resolveInAssign2Argument(assign, incompleteCode, expression.keywordKey, initial)
            is ElixirUnmatchedUnqualifiedNoArgumentsCall -> initial
            else -> {
                TODO()
            }
        }

    private fun resolveInAtomArgument(assign: Assign, incompleteCode: Boolean, expression: PsiElement, initial: List<ResolveResult>): List<ResolveResult> =
            when (expression) {
                is ElixirAccessExpression -> resolveInAtomArgument(assign, incompleteCode, expression.stripAccessExpression(), initial)
                is ElixirAtom -> if (expression.charListLine == null && expression.stringLine == null) {
                    val resolvedName = expression.node.lastChildNode.text
                    val assignName = assign.name

                    if (resolvedName.startsWith(assignName)) {
                        val validResult = resolvedName == assignName
                        initial + listOf(PsiElementResolveResult(expression, validResult))
                    } else {
                        null
                    }
                } else {
                    null
                } ?: initial
                else -> {
                    TODO()
                }
            }

    private fun resolveInLiveComponentCalls(assign: Assign, incompleteCode: Boolean, viewModule: Call): List<ResolveResult> {
        val searchScope = GlobalSearchScope.projectScope(assign.element.project).let { GlobalSearchScope.getScopeRestrictedByFileTypes(it, org.elixir_lang.leex.file.Type.INSTANCE) }
        val resolveResultList = mutableListOf<ResolveResult>()
        ReferencesSearch.search(viewModule, searchScope).forEach { psiReference ->
            liveComponentCall(psiReference)?.let { resolveResultList.addAll(resolveInLiveComponentCall(assign, incompleteCode, it)) }

            true
        }

        return resolveResultList
    }

    private fun liveComponentCall(psiReference: PsiReference): Call? = liveComponentCall(psiReference.element)

    private tailrec fun liveComponentCall(element: PsiElement): Call? =
            when (element) {
                is Arguments, is ElixirAccessExpression, is QualifiableAlias -> liveComponentCall(element.parent)
                is Call -> if ((element.functionName() == "live_component" && element.resolvedFinalArity() == 3) ||
                        // `live_modal` is auto-generated in `MyAppWeb.LiveHelpers` by the `mix phx.gen.live` generator`
                        // to turn a live component into modal dialog.  It calls `live_component`, so count it as a
                        // `live_component` call.
                        (element.functionName() == "live_modal" && element.resolvedFinalArity() == 3)  ) {
                    element
                } else {
                    null
                }
                else -> null
            }

    private fun resolveInLiveComponentCall(assign: Assign, incompleteCode: Boolean, liveComponentCall: Call): List<ResolveResult> =
            liveComponentCall
                    .finalArguments()
                    ?.last()
                    ?.let { resolveInLiveComponentAssigns(assign, incompleteCode, it) }
                    ?: emptyList()

    private tailrec fun resolveInLiveComponentAssigns(assign: Assign, incompleteCode: Boolean, assigns: PsiElement): List<ResolveResult> =
        when (assigns) {
            is QuotableKeywordList ->
                assigns.quotableKeywordPairList().flatMap { keywordPair ->
                    resolveInLiveComponentAssigns(assign, incompleteCode, keywordPair)
                }
            is QuotableKeywordPair -> resolveInLiveComponentAssigns(assign, incompleteCode, assigns.keywordKey)
            is ElixirKeywordKey -> {
                if (assigns.charListLine == null && assigns.stringLine == null) {
                    val resolvedName = assigns.text
                    val assignName = assign.name

                    if (resolvedName.startsWith(assignName)) {
                        val validResult = resolvedName == assignName

                        listOf(PsiElementResolveResult(assigns, validResult))
                    } else {
                        null
                    }
                } else {
                    null
                } ?: emptyList()
            }
            else -> emptyList()
        }

    private const val LIVE_ACTION = "live_action"
    private const val MYSELF = "myself"

    private fun resolveInPhoenixLiveView(assign: Assign, incompleteCode: Boolean): List<ResolveResult> {
        val assignName = assign.name

        return if (LIVE_ACTION.startsWith(assignName)) {
            resolveLiveAction(assign, incompleteCode)
        } else if (MYSELF.startsWith(assignName)) {
            resolveMyself(assign, incompleteCode)
        } else {
            emptyList()
        }
    }

    private fun resolveLiveAction(assign: Assign, incompleteCode: Boolean): List<ResolveResult> {
        val element = assign.element
        val globalSearchScope = globalSearchScope(element)
        val resolveResultList = mutableListOf<ResolveResult>()

        StubIndex.getInstance().processElements(AllName.KEY, "assign_action", element.project, globalSearchScope, NamedElement::class.java) { namedElement ->
            // use `StubBased<*>` to ignore decompiled
            if (namedElement is StubBased<*>) {
                val acc = resolveInCallDefinitionClause(assign, incompleteCode, namedElement, emptyList())
                resolveResultList.addAll(acc)
            }

            true
        }

        return resolveResultList
    }

    private fun resolveMyself(assign: Assign, incompleteCode: Boolean): List<ResolveResult> {
        val element = assign.element
        val globalSearchScope = globalSearchScope(element)
        val resolveResultList = mutableListOf<ResolveResult>()

        StubIndex.getInstance().processElements(AllName.KEY, "render_pending_components", element.project, globalSearchScope, NamedElement::class.java) { namedElement ->
            // use `StubBased<*>` to ignore decompiled
            if (namedElement is StubBased<*>) {
                val acc = resolveInRenderPendingComponentsClause(assign, incompleteCode, namedElement, emptyList())
                resolveResultList.addAll(acc)
            }

            true
        }

        return resolveResultList
    }

    private fun resolveInRenderPendingComponentsClause(assign: Assign, incompleteCode: Boolean, callDefinitionClause: Call, initial: List<ResolveResult>): List<ResolveResult> =
    resolveInCallDefinitionClause(assign, incompleteCode, callDefinitionClause, initial, ::resolveMyself)

    private tailrec fun resolveMyself(assign: Assign, incompleteCode: Boolean, expression: PsiElement, initial: List<ResolveResult>): List<ResolveResult> =
            when (expression) {
                is ElixirAccessExpression, is ElixirList, is ElixirStabBody, is ElixirTuple -> expression
                        .lastChild
                        .siblings(forward = false, withSelf = true)
                        .filter { it.node is CompositeElement }
                        .fold(initial) { acc, child ->
                            resolveMyself(assign, incompleteCode, child, acc)
                        }
                is ElixirAnonymousFunction -> resolveMyself(assign, incompleteCode, expression.stab, initial)
                is Match -> {
                    val rightOperand = expression.rightOperand()

                    if (rightOperand != null) {
                        resolveMyself(assign, incompleteCode, rightOperand, initial)
                    } else {
                        initial
                    }
                }
                is Arrow, is AtNonNumericOperation, is ElixirAtomKeyword, is ElixirEndOfExpression, is Pipe, is Two -> initial
                is Operation -> {
                    TODO()
                }
                is Call -> {
                    val arguments = expression.finalArguments()

                    val argumentsAcc = if (arguments != null) {
                        arguments.fold(initial) { acc, argument ->
                            resolveMyself(assign, incompleteCode, argument, acc)
                        }
                    } else {
                        initial
                    }

                    val stab = expression.doBlock?.stab

                    if (stab != null) {
                        resolveMyself(assign, incompleteCode, stab, argumentsAcc)
                    } else {
                        argumentsAcc
                    }
                }
                is ElixirMapOperation -> resolveMyself(assign, incompleteCode, expression.mapArguments, initial)
                is ElixirMapArguments -> {
                    val child = expression.mapConstructionArguments ?: expression.mapConstructionArguments

                    if (child != null) {
                        resolveMyself(assign, incompleteCode, child, initial)
                    } else {
                        initial
                    }
                }
                is ElixirMapConstructionArguments -> expression.arguments().fold(initial) { acc, argument ->
                    resolveMyself(assign, incompleteCode, argument, acc)
                }
                is ElixirStab -> {
                    val stabBody = expression.stabBody

                    if (stabBody != null) {
                        resolveMyself(assign, incompleteCode, stabBody, initial)
                    } else {
                        expression.stabOperationList.fold(initial) { acc, stabOperation ->
                            resolveMyself(assign, incompleteCode, stabOperation, acc)
                        }
                    }
                }
                is ElixirStabOperation -> {
                    val rightOperand = expression.rightOperand()

                    if (rightOperand != null) {
                        resolveMyself(assign, incompleteCode, rightOperand, initial)
                    } else {
                        initial
                    }
                }
                is QuotableKeywordList -> expression.quotableKeywordPairList().fold(initial) { acc, keywordPair ->
                    resolveMyself(assign, incompleteCode, keywordPair, acc)
                }
                is QuotableKeywordPair -> resolveMyself(assign, incompleteCode, expression.keywordKey, initial)
                is ElixirKeywordKey -> {
                    if (expression.charListLine == null && expression.stringLine == null) {
                        val resolvedName = expression.text
                        val assignName = assign.name

                        if (resolvedName.startsWith(assignName)) {
                            val validResult = resolvedName == assignName

                            initial + listOf(PsiElementResolveResult(expression, validResult))
                        } else {
                            null
                        }
                    } else {
                        null
                    } ?: initial
                }
                else -> {
                    TODO()
                }
            }
    private fun resolveInCallDefinitionClause(assign: Assign, incompleteCode: Boolean, callDefinitionClause: Call, initial: List<ResolveResult>, resolveInExpression: (assign: Assign, incompleteCode: Boolean, expression: PsiElement, initial: List<ResolveResult>) -> List<ResolveResult>): List<ResolveResult> =
            callDefinitionClause
                    .doBlock
                    ?.stab
                    ?.stabBody
                    ?.let { resolveInExpression(assign, incompleteCode, it, initial) }
                    ?: initial

    private fun globalSearchScope(element: PsiElement): GlobalSearchScope {
        val project = element.project

        // MUST use `originalFile` to get the PsiFile with a VirtualFile for decompiled elements
        return element.containingFile.originalFile.virtualFile?.let { elementVirtualFile ->
            val orderEntries =
                    ProjectRootManager
                            .getInstance(project)
                            .fileIndex
                            .getOrderEntriesForFile(elementVirtualFile)

            LibraryScopeCache.getInstance(project).getLibraryScope(orderEntries)
        } ?: GlobalSearchScope.allScope(project)
    }
}
