package org.elixir_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.tree.TokenSet
import org.elixir_lang.ElixirSyntaxHighlighter
import org.elixir_lang.eex.Language
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.CallDefinitionClause.`is`
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.psi.impl.identifierName
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.operation.*
import org.elixir_lang.reference.ModuleAttribute.Companion.isCallbackName
import org.elixir_lang.reference.ModuleAttribute.Companion.isDocumentationName
import org.elixir_lang.reference.ModuleAttribute.Companion.isSpecificationName
import org.elixir_lang.reference.ModuleAttribute.Companion.isTypeName
import org.elixir_lang.structure_view.element.CallDefinitionHead.Companion.stripAllOuterParentheses

/**
 * Annotates module attributes.
 */
class ModuleAttribute : Annotator, DumbAware {
    /*
     * Public Instance Methods
     */
    /**
     * Annotates the specified PSI element.
     * It is guaranteed to be executed in non-reentrant fashion.
     * I.e there will be no call of this method for this instance before previous call get completed.
     * Multiple instances of the annotator might exist simultaneously, though.
     *
     * @param element to annotate.
     * @param holder  the container which receives annotations created by the plugin.
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        // if there exists an EEx tag as a parent, we're in an EEx file
        if (!element.containingFile.viewProvider.languages.contains(Language.INSTANCE)) {
            element.accept(
                    object : PsiRecursiveElementVisitor() {
                        /*
                         *
                         * Instance Methods
                         *
                         */
                        /*
                         * Public Instance Methods
                         */
                        override fun visitElement(element: PsiElement) {
                            if (element is AtNonNumericOperation) {
                                visitMaybeUsage(element)
                            } else if (element is AtUnqualifiedNoParenthesesCall<*>) {
                                visitDeclaration(element)
                            }
                        }

                        /*
                         * Private Instance Methods
                         */
                        private fun visitDeclaration(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>) {
                            val atIdentifier = atUnqualifiedNoParenthesesCall.atIdentifier
                            val textRange = atIdentifier.textRange
                            val identifier = atIdentifier.identifierName()

                            when {
                                isCallbackName(identifier) -> {
                                    Highlighter.highlight(holder, textRange, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE)
                                    highlightCallback(atUnqualifiedNoParenthesesCall, holder)
                                }
                                isDocumentationName(identifier) -> {
                                    Highlighter.highlight(holder, textRange, ElixirSyntaxHighlighter.DOCUMENTATION_MODULE_ATTRIBUTE)
                                    highlightDocumentationText(atUnqualifiedNoParenthesesCall, holder)
                                }
                                isTypeName(identifier) -> {
                                    Highlighter.highlight(holder, textRange, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE)
                                    highlightType(atUnqualifiedNoParenthesesCall, holder)
                                }
                                isSpecificationName(identifier) -> {
                                    Highlighter.highlight(holder, textRange, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE)
                                    highlightSpecification(atUnqualifiedNoParenthesesCall, holder)
                                }
                                else -> {
                                    Highlighter.highlight(holder, textRange, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE)
                                }
                            }
                        }

                        private fun visitMaybeUsage(element: AtNonNumericOperation) {
                            element.operand()?.let { operand ->
                                if (operand !is ElixirAccessExpression) {
                                    visitUsage(element)
                                }
                            }
                        }

                        private fun visitUsage(atNonNumericOperation: AtNonNumericOperation) {
                            Highlighter.highlight(holder,
                                    atNonNumericOperation.textRange,
                                    ElixirSyntaxHighlighter.MODULE_ATTRIBUTE
                            )
                        }
                    }
            )
        }
    }

    /*
     * Private Instance Methods
     */
    private fun cannotHighlightTypes(element: PsiElement?) {
        error("Cannot highlight types", element)
    }

    private fun error(userMessage: String, element: PsiElement?) {
        Logger.error(this.javaClass, userMessage, element)
    }

    private fun highlightCallback(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>,
                                  annotationHolder: AnnotationHolder) {
        highlightSpecification(
                atUnqualifiedNoParenthesesCall,
                annotationHolder,
                ElixirSyntaxHighlighter.CALLBACK
        )
    }

    private fun highlightDocumentationText(
            atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>,
            holder: AnnotationHolder
    ) {
        val noParenthesesOneArgument: PsiElement = atUnqualifiedNoParenthesesCall.noParenthesesOneArgument
        val grandChildren = noParenthesesOneArgument.children

        grandChildren.singleOrNull()?.let { grandChild ->
            val greatGrandChildren = grandChild.children

            greatGrandChildren.singleOrNull()?.let { greatGrandChild ->
                when (greatGrandChild) {
                    is ElixirAtomKeyword -> {
                        val text = greatGrandChild.text

                        if (text == "false") {
                            holder
                                    .newAnnotation(HighlightSeverity.WEAK_WARNING, "Will make documented invisible to the documentation extraction tools like ExDoc.")
                                    .range(greatGrandChild)
                                    .create()
                        }
                    }
                    is Heredoc -> {
                        val heredocLineList = greatGrandChild.heredocLineList

                        for (bodied in heredocLineList) {
                            val body = bodied.body
                            highlightFragments(
                                    greatGrandChild,
                                    body,
                                    holder
                            )
                        }
                    }
                    is Line -> {
                        val body = greatGrandChild.body
                        highlightFragments(
                                greatGrandChild,
                                body,
                                holder
                        )
                    }
                }
            }
        }
    }

    /**
     * Highlights fragment ASTNodes under `body` that have fragment type from `fragmented.getFragmentType()`.
     *
     * @param fragmented        supplies fragment type
     * @param body              contains fragments
     * @param annotationHolder  the container which receives annotations created by the plugin.
     */
    private fun highlightFragments(fragmented: Fragmented,
                                   body: Body,
                                   annotationHolder: AnnotationHolder) {
        val bodyNode = body.node
        val fragmentNodes = bodyNode.getChildren(
                TokenSet.create(fragmented.fragmentType)
        )
        for (fragmentNode in fragmentNodes) {
            Highlighter.highlight(annotationHolder,
                    fragmentNode.textRange,
                    ElixirSyntaxHighlighter.DOCUMENTATION_TEXT
            )
        }
    }

    /**
     * Highlights the function call name as a `ElixirSyntaxHighlighter.SPECIFICATION
     */
    private fun highlightSpecification(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>,
                                       annotationHolder: AnnotationHolder) {
        highlightSpecification(
                atUnqualifiedNoParenthesesCall,
                annotationHolder,
                ElixirSyntaxHighlighter.SPECIFICATION
        )
    }

    /**
     * Highlights the function name of the declared @type, @typep, or @opaque as an [ElixirSyntaxHighlighter.TYPE]
     * and the its parameters as [ElixirSyntaxHighlighter.TYPE_PARAMETER].
     */
    private fun highlightType(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>,
                              annotationHolder: AnnotationHolder) {
        val noParenthesesOneArgument: PsiElement = atUnqualifiedNoParenthesesCall.noParenthesesOneArgument
        val grandChildren = noParenthesesOneArgument.children

        grandChildren.singleOrNull()?.let { grandChild ->
            when (grandChild) {
                /* Match is invalid.  It will be marked by MatchOperatorInsteadOfTypeOperator inspection as an error */
                is Match, is Type -> {
                    val infix = grandChild as Infix
                    val leftOperand: PsiElement? = infix.leftOperand()
                    var typeParameterNameSet: Set<String?> = emptySet<String>()

                    if (leftOperand is Call) {
                        highlightTypeName(leftOperand, annotationHolder)
                        if (leftOperand is ElixirMatchedUnqualifiedParenthesesCall) {
                            typeParameterNameSet = highlightTypeLeftOperand(
                                    leftOperand,
                                    annotationHolder
                            )
                        } else if (leftOperand !is ElixirMatchedUnqualifiedNoArgumentsCall) {
                            cannotHighlightTypes(leftOperand)
                        }
                    } else {
                        cannotHighlightTypes(leftOperand)
                    }

                    val rightOperand: PsiElement? = infix.rightOperand()

                    if (rightOperand != null) {
                        highlightTypesAndTypeParameterUsages(
                                rightOperand,
                                typeParameterNameSet,
                                annotationHolder,
                                ElixirSyntaxHighlighter.TYPE
                        )
                    } else {
                        null
                    }
                }
                is ElixirMatchedUnqualifiedParenthesesCall -> {
                    // seen as `unquote(ast)`, but could also be just the beginning of typing
                    if (Function.UNQUOTE == grandChild.functionName()) {
                        grandChild.secondaryArguments()?.map { it!! }?.let { secondaryArguments ->
                            val typeParameterNameSet = typeTypeParameterNameSet(secondaryArguments)
                            highlightTypesAndTypeTypeParameterDeclarations(
                                    secondaryArguments,
                                    typeParameterNameSet,
                                    annotationHolder,
                                    ElixirSyntaxHighlighter.TYPE
                            )
                        } ?: return
                    } else {
                        highlightTypeName(grandChild, annotationHolder)

                        // Assume it's `@type foo(bar)` before completed as `@type foo(bar) :: bar`
                        highlightTypeLeftOperand(
                                grandChild,
                                annotationHolder
                        )
                    }
                }
                is QuotableKeywordList -> {
                    grandChild.quotableKeywordPairList().singleOrNull()?.let { quotableKeywordPair ->
                        // occurs when user does `my_type: definition` instead of `my_type :: definition`
                        val quotableKeywordKey = quotableKeywordPair.keywordKey

                        if (quotableKeywordKey is ElixirKeywordKey) {
                            Highlighter.highlight(annotationHolder,
                                    quotableKeywordKey.textRange,
                                    ElixirSyntaxHighlighter.TYPE
                            )
                        }

                        val quotableKeywordValue = quotableKeywordPair.keywordValue
                        highlightTypesAndTypeParameterUsages(
                                quotableKeywordValue, emptySet<String>(),
                                annotationHolder,
                                ElixirSyntaxHighlighter.TYPE
                        )
                    }
                    // Otherwise, allow the normal, non-type highlighting
                }
                is UnqualifiedNoArgumentsCall<*> -> {
                    // assume it's a type name that is being typed
                    val grandChildCall = grandChild as Call
                    grandChildCall.functionNameElement()?.let { functionNameElement ->
                        Highlighter.highlight(annotationHolder,
                                functionNameElement.textRange,
                                ElixirSyntaxHighlighter.TYPE
                        )
                        Unit
                    }
                }
                is UnqualifiedNoParenthesesCall<*> -> {
                    /* Pretend that `::` separates the functionNameElement from the arguments, so that
                       ```
                       @type coefficient non_neg_integer | :qNaN | :sNaN | :inf
                       ```
                       is retreated like
                       ```
                       @type coefficient :: non_neg_integer | :qNaN | :sNaN | :inf
                       ```
                     */

                    grandChild.functionNameElement()?.let { functionNameElement ->
                        Highlighter.highlight(annotationHolder,
                                functionNameElement.textRange,
                                ElixirSyntaxHighlighter.TYPE
                        )
                        Unit
                    }

                    highlightTypesAndTypeParameterUsages(
                            grandChild.noParenthesesOneArgument, emptySet<String>(),
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    )
                }
                else -> {
                    cannotHighlightTypes(grandChild)
                }
            }
        }
    }

    private fun highlightTypeError(element: PsiElement,
                                   message: String,
                                   annotationHolder: AnnotationHolder) {
        annotationHolder.newAnnotation(HighlightSeverity.ERROR, message).range(element).create()
    }

    private fun highlightTypeLeftOperand(call: ElixirMatchedUnqualifiedParenthesesCall,
                                         annotationHolder: AnnotationHolder): Set<String?> {
        val primaryArguments = call.primaryArguments()

        /* if there are secondaryArguments, then it is the type parameters as in
           `@type quote(type_name)(param1, param2) :: {param1, param2}` */
        return call.secondaryArguments()?.map { it!! }?.let { secondaryArguments ->
            typeTypeParameterNameSet(secondaryArguments).also { typeParameterNameSet ->
                highlightTypesAndTypeParameterUsages(
                        primaryArguments, emptySet<String>(),
                        annotationHolder,
                        ElixirSyntaxHighlighter.TYPE
                )
                highlightTypesAndTypeTypeParameterDeclarations(
                        secondaryArguments,
                        typeParameterNameSet,
                        annotationHolder,
                        ElixirSyntaxHighlighter.TYPE
                )
            }
        } ?: typeTypeParameterNameSet(primaryArguments).also { typeParameterNameSet ->
            highlightTypesAndTypeTypeParameterDeclarations(
                    primaryArguments,
                    typeParameterNameSet,
                    annotationHolder,
                    ElixirSyntaxHighlighter.TYPE
            )
        }
    }

    private fun highlightTypeName(call: Call, annotationHolder: AnnotationHolder) {
        call.functionNameElement()?.let { functionNameElement ->
            Highlighter.highlight(annotationHolder,
                    functionNameElement.textRange,
                    ElixirSyntaxHighlighter.TYPE
            )
            Unit
        }
    }

    private fun highlightTypesAndTypeTypeParameterDeclarations(alias: ElixirAlias,
                                                               typeParameterNameSet: Set<String?>,
                                                               annotationHolder: AnnotationHolder,
                                                               typeTextAttributesKey: TextAttributesKey) {
        val name = alias.name

        val textAttributesKey = if (typeParameterNameSet.contains(name)) {
            ElixirSyntaxHighlighter.TYPE_PARAMETER
        } else {
            typeTextAttributesKey
        }

        Highlighter.highlight(annotationHolder, alias.textRange, textAttributesKey)
    }

    private fun highlightTypesAndTypeTypeParameterDeclarations(psiElement: ElixirUnmatchedUnqualifiedNoArgumentsCall,
                                                               typeParameterNameSet: Set<String?>,
                                                               annotationHolder: AnnotationHolder,
                                                               typeTextAttributesKey: TextAttributesKey) {
        val name = psiElement.text

        val textAttributesKey = if (typeParameterNameSet.contains(name)) {
            ElixirSyntaxHighlighter.TYPE_PARAMETER
        } else {
            typeTextAttributesKey
        }

        Highlighter.highlight(annotationHolder, psiElement.textRange, textAttributesKey)
    }

    private fun highlightTypesAndTypeTypeParameterDeclarations(typeOperation: ElixirUnmatchedTypeOperation,
                                                               typeParameterNameSet: Set<String?>,
                                                               annotationHolder: AnnotationHolder,
                                                               typeTextAttributesKey: TextAttributesKey) {
        typeOperation.leftOperand()?.let {
            highlightTypesAndTypeTypeParameterDeclarations(it, typeParameterNameSet, annotationHolder, typeTextAttributesKey)
        }
    }

    private fun highlightTypesAndTypeTypeParameterDeclarations(psiElement: PsiElement,
                                                               typeParameterNameSet: Set<String?>,
                                                               annotationHolder: AnnotationHolder,
                                                               typeTextAttributesKey: TextAttributesKey) {
        when (psiElement) {
            is ElixirAccessExpression, is ElixirList, is ElixirTuple -> {
                highlightTypesAndTypeTypeParameterDeclarations(
                        psiElement.children,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            is ElixirAlias -> {
                highlightTypesAndTypeTypeParameterDeclarations(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            is ElixirUnmatchedUnqualifiedNoArgumentsCall -> {
                highlightTypesAndTypeTypeParameterDeclarations(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            is ElixirUnmatchedTypeOperation -> {
                highlightTypesAndTypeTypeParameterDeclarations(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            else -> {
                if (psiElement !is ElixirAtomKeyword) {
                    error("Cannot highlight types and type parameter declarations", psiElement)
                }
            }
        }
    }

    private fun highlightTypesAndTypeTypeParameterDeclarations(
            psiElements: Array<PsiElement>,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            typeTextAttributesKey: TextAttributesKey) {
        for (psiElement in psiElements) {
            highlightTypesAndTypeTypeParameterDeclarations(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        }
    }

    private fun highlightTypesAndTypeTypeParameterDeclarations(
            psiElements: List<PsiElement>,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            typeTextAttributesKey: TextAttributesKey) {
        for (psiElement in psiElements) {
            highlightTypesAndTypeTypeParameterDeclarations(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        }
    }

    /**
     * Recursively highlights the types under `atUnqualifiedNoParenthesesCall`.
     *
     * @param leftMostFunctionNameTextAttributesKey      the [ElixirSyntaxHighlighter] [TextAttributesKey] for the
     * name of the callback, type, or function being declared
     */
    private fun highlightSpecification(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>,
                                       annotationHolder: AnnotationHolder,
                                       leftMostFunctionNameTextAttributesKey: TextAttributesKey) {
        val noParenthesesOneArgument: PsiElement = atUnqualifiedNoParenthesesCall.noParenthesesOneArgument

        noParenthesesOneArgument.children.singleOrNull()?.let { grandChild ->
            if (grandChild is Type) {
                val infix = grandChild as Infix
                val leftOperand: PsiElement? = infix.leftOperand()

                if (leftOperand is Call) {
                    leftOperand.functionNameElement()?.let { functionNameElement ->
                        Highlighter.highlight(annotationHolder,
                                functionNameElement.textRange,
                                leftMostFunctionNameTextAttributesKey
                        )
                        Unit
                    }

                    leftOperand.primaryArguments()?.let { primaryArguments ->
                        highlightTypesAndTypeParameterUsages(
                                primaryArguments.map { it!! }, emptySet<String>(),
                                annotationHolder,
                                ElixirSyntaxHighlighter.TYPE
                        )
                    }

                    leftOperand.secondaryArguments()?.let { secondaryArguments ->
                        highlightTypesAndTypeParameterUsages(
                                secondaryArguments.map { it!! }, emptySet<String>(),
                                annotationHolder,
                                ElixirSyntaxHighlighter.TYPE
                        )
                    }
                }

                infix.rightOperand()?.let { rightOperand ->
                    highlightTypesAndTypeParameterUsages(
                            rightOperand, emptySet<String>(),
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    )
                }
            } else if (grandChild is ElixirMatchedWhenOperation) {
                val rightOperand: PsiElement? = grandChild.rightOperand()
                val typeParameterNameSet: Set<String?>
                typeParameterNameSet = rightOperand?.let { specificationTypeParameterNameSet(it) } ?: emptySet<String>()

                when (val leftOperand: PsiElement? = grandChild.leftOperand()) {
                    is Type -> {
                        leftOperand.leftOperand()?.let { typeOperationLeftOperand ->
                            val strippedTypeOperationLeftOperand = stripAllOuterParentheses(typeOperationLeftOperand)

                            if (strippedTypeOperationLeftOperand is Call) {
                                highlightSpecification(
                                        strippedTypeOperationLeftOperand,
                                        annotationHolder,
                                        leftMostFunctionNameTextAttributesKey,
                                        leftMostFunctionNameTextAttributesKey,
                                        typeParameterNameSet
                                )
                            } else {
                                cannotHighlightTypes(strippedTypeOperationLeftOperand)
                            }
                        }

                        leftOperand.rightOperand()?.let { matchedTypeOperationRightOperand ->
                            highlightTypesAndTypeParameterUsages(
                                    matchedTypeOperationRightOperand,
                                    typeParameterNameSet,
                                    annotationHolder,
                                    ElixirSyntaxHighlighter.TYPE
                            )
                        }
                    }
                    is Call -> {
                        highlightSpecification(
                                leftOperand,
                                annotationHolder,
                                leftMostFunctionNameTextAttributesKey,
                                leftMostFunctionNameTextAttributesKey,
                                typeParameterNameSet
                        )
                    }
                    else -> {
                        cannotHighlightTypes(leftOperand)
                    }
                }

                if (rightOperand != null) {
                    highlightTypesAndSpecificationTypeParameterDeclarations(
                            rightOperand,
                            typeParameterNameSet,
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    )
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    private fun highlightSpecification(call: Call,
                                       annotationHolder: AnnotationHolder,
                                       leftMostFunctionNameTextAttributesKey: TextAttributesKey,
                                       leftMostFunctionArgumentsTextAttributesKey: TextAttributesKey,
                                       typeParameterNameSet: Set<String?>) {
        call.functionNameElement()?.let {
            Highlighter.highlight(annotationHolder,
                    it.textRange,
                    leftMostFunctionNameTextAttributesKey
            )
            Unit
        }

        call.primaryArguments()?.let { highlightTypesAndTypeParameterUsages(it.map { element -> element!! }, typeParameterNameSet, annotationHolder, leftMostFunctionArgumentsTextAttributesKey) }
        call.secondaryArguments()?.let { highlightTypesAndTypeParameterUsages(it.map { element -> element!! }, typeParameterNameSet, annotationHolder, leftMostFunctionArgumentsTextAttributesKey) }
    }

    private fun highlightTypesAndSpecificationTypeParameterDeclarations(quotableKeywordPair: QuotableKeywordPair,
                                                                        typeParameterNameSet: Set<String?>,
                                                                        annotationHolder: AnnotationHolder,
                                                                        typeTextAttributesKey: TextAttributesKey) {
        val keywordKey: PsiElement = quotableKeywordPair.keywordKey

        if (typeParameterNameSet.contains(keywordKey.text)) {
            Highlighter.highlight(annotationHolder, keywordKey.textRange, ElixirSyntaxHighlighter.TYPE_PARAMETER)
        } else {
            highlightTypesAndTypeParameterUsages(
                    keywordKey,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        }

        highlightTypesAndTypeParameterUsages(
                quotableKeywordPair.keywordValue,
                typeParameterNameSet,
                annotationHolder,
                typeTextAttributesKey
        )
    }

    /**
     * `::` is an error, but can happen due to user error either because they think `::` should work in the `when`
     * clause or when overtyping the `:` for the proper keyword list used in `when`.
     */
    private fun highlightTypesAndSpecificationTypeParameterDeclarations(type: Type,
                                                                        typeParameterNameSet: Set<String?>,
                                                                        annotationHolder: AnnotationHolder,
                                                                        typeTextAttributesKey: TextAttributesKey) {
        type.leftOperand()?.let {
            if (typeParameterNameSet.contains(it.text)) {
                Highlighter.highlight(annotationHolder, it.textRange, ElixirSyntaxHighlighter.TYPE_PARAMETER)
                Unit
            } else {
                highlightTypesAndTypeParameterUsages(
                        it,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
        }

        type.rightOperand()?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, typeTextAttributesKey) }
    }

    private fun highlightTypesAndSpecificationTypeParameterDeclarations(psiElement: PsiElement,
                                                                        typeParameterNameSet: Set<String?>,
                                                                        annotationHolder: AnnotationHolder,
                                                                        typeTextAttributesKey: TextAttributesKey) {
        when (psiElement) {
            is ElixirAccessExpression, is ElixirKeywords, is ElixirList, is ElixirNoParenthesesKeywords -> {
                highlightTypesAndSpecificationTypeParameterDeclarations(
                        psiElement.children,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            is QuotableKeywordPair -> {
                highlightTypesAndSpecificationTypeParameterDeclarations(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            is Type -> {
                highlightTypesAndSpecificationTypeParameterDeclarations(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            is UnqualifiedNoArgumentsCall<*> -> {
                highlightTypesAndSpecificationTypeParameterDeclarations(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder
                )
            }
            is UnqualifiedNoParenthesesCall<*> -> {
                highlightTypesAndSpecificationTypeParameterDeclarations(
                        psiElement
                )
            }
            else -> {
                error("Cannot highlight types and specification type parameter declarations", psiElement)
            }
        }
    }

    private fun highlightTypesAndSpecificationTypeParameterDeclarations(psiElements: Array<PsiElement>,
                                                                        typeParameterNameSet: Set<String?>,
                                                                        annotationHolder: AnnotationHolder,
                                                                        typeTextAttributesKey: TextAttributesKey) {
        for (psiElement in psiElements) {
            highlightTypesAndSpecificationTypeParameterDeclarations(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        }
    }

    private fun highlightTypesAndSpecificationTypeParameterDeclarations(
            unqualifiedNoArgumentsCall: UnqualifiedNoArgumentsCall<*>,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder) {
        if (typeParameterNameSet.contains(unqualifiedNoArgumentsCall.functionName())) {
            val functionNameElement = unqualifiedNoArgumentsCall.functionNameElement()
            Highlighter.highlight(annotationHolder,
                    functionNameElement.textRange,
                    ElixirSyntaxHighlighter.TYPE_PARAMETER
            )
        }
    }

    private fun highlightTypesAndSpecificationTypeParameterDeclarations(
            unqualifiedNoParenthesesCall: UnqualifiedNoParenthesesCall<*>) {
        if (!`is`(unqualifiedNoParenthesesCall)) {
            error(
                    "Cannot highlight types and specification type parameter declarations in UnqualifiedNoParenthesesCall that is not a call definition clause", unqualifiedNoParenthesesCall)
        }
    }

    private fun highlightTypesAndTypeParameterUsages(arguments: Arguments,
                                                     typeParameterNameSet: Set<String?>,
                                                     annotationHolder: AnnotationHolder,
                                                     textAttributesKey: TextAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                arguments.arguments(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        )
    }

    private fun highlightTypesAndTypeParameterUsages(decimalFloat: ElixirDecimalFloat,
                                                     annotationHolder: AnnotationHolder) {
        val parent = decimalFloat.parent
        var message: String? = null

        if (parent is ElixirAccessExpression) {
            val grandParent = parent.getParent()
            if (grandParent is Two) {
                grandParent.operator()?.let { operator ->
                    val rangeOperators = operator.node.getChildren(TokenSet.create(ElixirTypes.RANGE_OPERATOR))

                    if (rangeOperators.isNotEmpty()) {
                        message = "Floats aren't allowed in Ranges"
                    }
                }
            }
        } else {
            cannotHighlightTypes(decimalFloat)
        }

        if (message == null) {
            message = "Float literals are not allowed in types: use float() instead"
        }

        highlightTypeError(decimalFloat, message!!, annotationHolder)
    }

    private fun highlightTypesAndTypeParameterUsages(mapOperation: ElixirMapOperation,
                                                     typeParameterNameSet: Set<String?>,
                                                     annotationHolder: AnnotationHolder,
                                                     typeTextAttributesKey: TextAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                mapOperation.mapArguments,
                typeParameterNameSet,
                annotationHolder,
                typeTextAttributesKey
        )
    }

    private fun highlightTypesAndTypeParameterUsages(mapUpdateArguments: ElixirMapUpdateArguments,
                                                     typeParameterNameSet: Set<String?>,
                                                     annotationHolder: AnnotationHolder,
                                                     typeTextAttributesKey: TextAttributesKey) {
        for (child in mapUpdateArguments.children) {
            if (child !is Operator) {
                highlightTypesAndTypeParameterUsages(
                        child,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
        }
    }

    private fun highlightTypesAndTypeParameterUsages(
            stabOperation: ElixirStabOperation,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            typeTextAttributesKey: TextAttributesKey) {
        val stabParenthesesSignature = stabOperation.stabParenthesesSignature

        if (stabParenthesesSignature != null) {
            highlightTypesAndTypeParameterUsages(
                    stabParenthesesSignature,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else {
            stabOperation.stabNoParenthesesSignature?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, typeTextAttributesKey) }
        }

        stabOperation.stabBody?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, typeTextAttributesKey) }
    }

    private fun highlightTypesAndTypeParameterUsages(
            stabParenthesesSignature: ElixirStabParenthesesSignature,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            typeTextAttributesKey: TextAttributesKey) {
        val children = stabParenthesesSignature.children

        if (children.size == 1) {
            highlightTypesAndTypeParameterUsages(
                    children[0],
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (children.size != 3) {
            error("Cannot highlight types and type parameter usages", stabParenthesesSignature)
        }
    }

    private fun highlightTypesAndTypeParameterUsages(
            structOperation: ElixirStructOperation,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            typeTextAttributesKey: TextAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                structOperation.mapArguments,
                typeParameterNameSet,
                annotationHolder,
                typeTextAttributesKey
        )
    }

    private fun highlightTypesAndTypeParameterUsages(
            infix: Infix,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            typeTextAttributesKey: TextAttributesKey) {
        infix.leftOperand()?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, typeTextAttributesKey) }
        infix.rightOperand()?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, typeTextAttributesKey) }
    }

    private fun highlightTypesAndTypeParameterUsages(psiElement: PsiElement,
                                                     typeParameterNameSet: Set<String?>,
                                                     annotationHolder: AnnotationHolder,
                                                     typeTextAttributesKey: TextAttributesKey) {
        when {
            psiElement is Arguments -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            psiElement is ElixirAccessExpression || psiElement is ElixirAssociationsBase || psiElement is ElixirAssociations || psiElement is ElixirContainerAssociationOperation || psiElement is ElixirKeywordPair || psiElement is ElixirKeywords || psiElement is ElixirList || psiElement is ElixirMapArguments || psiElement is ElixirMapConstructionArguments || psiElement is ElixirNoParenthesesArguments || psiElement is ElixirParentheticalStab || psiElement is ElixirStab || psiElement is ElixirStabBody || psiElement is ElixirStabNoParenthesesSignature || psiElement is ElixirTuple -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement.children,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            psiElement is ElixirDecimalFloat -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        annotationHolder
                )
            }
            psiElement is ElixirMapOperation -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            psiElement is ElixirMapUpdateArguments -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            psiElement is ElixirRelativeIdentifier || psiElement is UnqualifiedNoArgumentsCall<*> -> {
                // highlight entire element
                val name = psiElement.text
                val textAttributesKey: TextAttributesKey
                textAttributesKey = if (typeParameterNameSet.contains(name)) {
                    ElixirSyntaxHighlighter.TYPE_PARAMETER
                } else {
                    typeTextAttributesKey
                }
                Highlighter.highlight(annotationHolder, psiElement.textRange, textAttributesKey)
            }
            psiElement is ElixirStabParenthesesSignature -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            psiElement is ElixirStabOperation -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            psiElement is ElixirStructOperation -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            psiElement is InterpolatedString -> {
                highlightTypeError(psiElement, "Strings aren't allowed in types", annotationHolder)
            }
            psiElement is Infix && psiElement !is When -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            psiElement is QualifiedParenthesesCall<*> -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            psiElement is QualifiedAlias -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            psiElement is QualifiedNoArgumentsCall<*> -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            psiElement is QualifiedNoParenthesesCall<*> -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            psiElement is UnqualifiedNoParenthesesCall<*> -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            psiElement is UnqualifiedParenthesesCall<*> -> {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
            else -> {
                if (!( /* Occurs in the case of typing a {@code @type name ::} above a {@code @doc <HEREDOC>} and the
                   {@code @doc <HEREDOC>} is interpreted as the right-operand of {@code ::} */psiElement is AtUnqualifiedNoParenthesesCall<*> ||  // leave normal highlighting
                                psiElement is BracketOperation ||
                                psiElement is ElixirAlias ||
                                psiElement is ElixirAtom ||
                                psiElement is ElixirAtomKeyword ||
                                psiElement is ElixirBitString ||
                                psiElement is ElixirCharListLine ||
                                psiElement is ElixirCharToken ||
                                psiElement is ElixirDecimalWholeNumber ||
                                psiElement is ElixirKeywordKey ||  /* happens when :: is typed in `@spec` above function clause that uses `do:` */
                                psiElement is ElixirNoParenthesesKeywords ||
                                psiElement is ElixirUnaryNumericOperation ||
                                psiElement is ElixirMatchedUnaryOperation ||
                                psiElement is ElixirVariable ||
                                psiElement is ElixirUnmatchedUnaryOperation ||
                                psiElement is When)) {
                    cannotHighlightTypes(psiElement)
                }
            }
        }
    }

    private fun highlightTypesAndTypeParameterUsages(
            psiElements: Array<PsiElement>,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            textAttributesKey: TextAttributesKey) {
        for (psiElement in psiElements) {
            highlightTypesAndTypeParameterUsages(psiElement, typeParameterNameSet, annotationHolder, textAttributesKey)
        }
    }

    private fun highlightTypesAndTypeParameterUsages(
            psiElements: List<PsiElement>,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            textAttributesKey: TextAttributesKey) {
        for (psiElement in psiElements) {
            highlightTypesAndTypeParameterUsages(psiElement, typeParameterNameSet, annotationHolder, textAttributesKey)
        }
    }

    private fun highlightTypesAndTypeParameterUsages(qualifiedAlias: QualifiedAlias,
                                                     typeParameterNameSet: Set<String?>,
                                                     annotationHolder: AnnotationHolder,
                                                     textAttributesKey: TextAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                qualifiedAlias.firstChild,
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        )
        highlightTypesAndTypeParameterUsages(
                qualifiedAlias.lastChild,
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        )
    }

    private fun highlightTypesAndTypeParameterUsages(
            qualifiedNoArgumentsCall: QualifiedNoArgumentsCall<*>,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            textAttributesKey: TextAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                qualifiedNoArgumentsCall.firstChild,
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        )
        highlightTypesAndTypeParameterUsages(
                qualifiedNoArgumentsCall.relativeIdentifier,
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        )
    }

    private fun highlightTypesAndTypeParameterUsages(
            qualifiedNoParenthesesCall: QualifiedNoParenthesesCall<*>,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            textAttributesKey: TextAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                qualifiedNoParenthesesCall.firstChild,
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        )
        highlightTypesAndTypeParameterUsages(
                qualifiedNoParenthesesCall.relativeIdentifier,
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        )
        highlightTypesAndTypeParameterUsages(
                qualifiedNoParenthesesCall.primaryArguments(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        )
    }

    private fun highlightTypesAndTypeParameterUsages(
            qualifiedParenthesesCall: QualifiedParenthesesCall<*>,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            textAttributesKey: TextAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                qualifiedParenthesesCall.firstChild,
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        )
        highlightTypesAndTypeParameterUsages(
                qualifiedParenthesesCall.relativeIdentifier,
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        )
        highlightTypesAndTypeParameterUsages(
                qualifiedParenthesesCall.primaryArguments(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        )
        qualifiedParenthesesCall.secondaryArguments()?.let { highlightTypesAndTypeParameterUsages(it.map { element -> element!! }, typeParameterNameSet, annotationHolder, textAttributesKey) }
    }

    private fun highlightTypesAndTypeParameterUsages(
            unqualifiedNoParenthesesCall: UnqualifiedNoParenthesesCall<*>,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            typeTextAttributesKey: TextAttributesKey) {
        val functionNameElement = unqualifiedNoParenthesesCall.functionNameElement()

        if (functionNameElement != null) {
            Highlighter.highlight(annotationHolder, functionNameElement.textRange, typeTextAttributesKey)
            highlightTypesAndTypeParameterUsages(
                    unqualifiedNoParenthesesCall.primaryArguments(),
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else {
            error("Cannot highlight types and type parameter usages", unqualifiedNoParenthesesCall)
        }
    }

    private fun highlightTypesAndTypeParameterUsages(
            unqualifiedParenthesesCall: UnqualifiedParenthesesCall<*>,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            typeTextAttributesKey: TextAttributesKey) {
        if (!unqualifiedParenthesesCall.isCalling(Module.KERNEL, Function.UNQUOTE, 1)) {
            val functionNameElement = unqualifiedParenthesesCall.functionNameElement()

            if (functionNameElement != null) {
                Highlighter.highlight(annotationHolder, functionNameElement.textRange, typeTextAttributesKey)
                highlightTypesAndTypeParameterUsages(
                        unqualifiedParenthesesCall.primaryArguments(),
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
                unqualifiedParenthesesCall
                        .secondaryArguments()
                        ?.map { it!! }
                        ?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, typeTextAttributesKey) }
                        ?: return
            } else {
                error("Cannot highlight types and type parameter usages", unqualifiedParenthesesCall)
            }
        }
    }

    private fun specificationTypeParameterNameSet(keywordPair: ElixirKeywordPair): Set<String?> =
            setOf(keywordPair.keywordKey.text)

    private fun specificationTypeParameterNameSet(noParenthesesKeywordPair: ElixirNoParenthesesKeywordPair): Set<String?> =
            setOf(noParenthesesKeywordPair.keywordKey.text)

    /**
     * A type operator is an error, keyword pairs should be used for `when type: definition` for expression-local types,
     * but using `::` is a common error, so support it.
     */
    private fun specificationTypeParameterNameSet(type: Type): Set<String?> =
            type.leftOperand()?.let {
                setOf(it.text)
            } ?: let {
                error("Type does not have a left operand", type)
                emptySet<String>()
            }

    private fun specificationTypeParameterNameSet(psiElement: PsiElement): Set<String?> =
            when (psiElement) {
                is ElixirAccessExpression, is ElixirKeywords, is ElixirList, is ElixirNoParenthesesKeywords -> {
                    specificationTypeParameterNameSet(psiElement.children)
                }
                is ElixirKeywordPair -> {
                    specificationTypeParameterNameSet(psiElement)
                }
                is Type -> {
                    specificationTypeParameterNameSet(psiElement)
                }
                is ElixirNoParenthesesKeywordPair -> {
                    specificationTypeParameterNameSet(psiElement)
                }
                is UnqualifiedNoArgumentsCall<*> -> {
                    specificationTypeParameterNameSet(psiElement)
                }
                is UnqualifiedNoParenthesesCall<*> -> {
                    specificationTypeParameterNameSet(psiElement)
                }
                else -> {
                    error("Cannot extract specification type parameter name set", psiElement)
                    emptySet<String>()
                }
            }

    private fun specificationTypeParameterNameSet(psiElements: Array<PsiElement>): Set<String?> =
            psiElements.flatMapTo(mutableSetOf()) {
                specificationTypeParameterNameSet(it)
            }

    /**
     * Occurs temporarily while typing before `:` in KeywordPairs after the `when`, such as in
     * `@spec foo(id) :: id when id` before finishing typing `@spec foo(id) :: id when id: String.t`.
     */
    private fun specificationTypeParameterNameSet(unqualifiedNoArgumentsCall: UnqualifiedNoArgumentsCall<*>): Set<String?> =
            unqualifiedNoArgumentsCall.functionName()?.let {
                setOf(it)
            } ?: emptySet<String>()

    private fun specificationTypeParameterNameSet(
            unqualifiedNoParenthesesCall: UnqualifiedNoParenthesesCall<*>
    ): Set<String?> {
        if (!`is`(unqualifiedNoParenthesesCall)) {
            error(
                    "Cannot extract specification type parameter name set from " +
                            "unqualifiedNoParenthesesCall that is a not a call definition clause",
                    unqualifiedNoParenthesesCall
            )
        }
        return emptySet<String>()
    }

    /**
     * Assume bare aliases are incorrectly capitalized type parameters, say from someone's that's used to generics
     * in Java.
     *
     * See https://github.com/KronicDeth/intellij-elixir/issues/694
     */
    private fun typeTypeParameterNameSet(alias: ElixirAlias): Set<String> = setOf(alias.name)

    private fun typeTypeParameterNameSet(tuple: ElixirTuple): Set<String> {
        var typeParameterNameSet: Set<String>? = null
        val children = tuple.children

        if (children.size == 3) {
            val firstChild = children[0]
            if (firstChild is UnqualifiedNoArgumentsCall<*>) {
                val strippedSecondChild = children[1].stripAccessExpression()
                if (strippedSecondChild is ElixirList) {
                    val strippedThirdChild = children[2].stripAccessExpression()
                    if (strippedThirdChild is ElixirAtomKeyword && strippedThirdChild.getText() == "nil") {
                        typeParameterNameSet = setOf(firstChild.getText())
                    }
                }
            }
        }

        if (typeParameterNameSet == null) {
            error("Cannot extract type type parameter name set", tuple)
            typeParameterNameSet = emptySet()
        }

        return typeParameterNameSet
    }

    private fun typeTypeParameterNameSet(psiElement: PsiElement): Set<String> =
            when (psiElement) {
                is ElixirAccessExpression -> {
                    typeTypeParameterNameSet(psiElement.getChildren())
                }
                is ElixirAlias -> {
                    /* Assume bare aliases are incorrectly capitalized type parameters, say from someone's that used to generics
                       in Java.

                       See https://github.com/KronicDeth/intellij-elixir/issues/694 */
                    typeTypeParameterNameSet(psiElement)
                }
                is ElixirTuple -> {
                    typeTypeParameterNameSet(psiElement)
                }
                is ElixirUnmatchedTypeOperation -> typeTypeParameterNameSet(psiElement)
                is ElixirUnmatchedUnqualifiedNoArgumentsCall -> {
                    setOf(psiElement.getText())
                }
                else -> {
                    error("Cannot extract type type parameter name set", psiElement)
                    emptySet<String>()
                }
            }

    private fun typeTypeParameterNameSet(psiElements: Array<PsiElement>): Set<String> =
            psiElements.flatMapTo(mutableSetOf()) {
                typeTypeParameterNameSet(it)
            }

    private fun typeTypeParameterNameSet(psiElements: List<PsiElement>): Set<String> =
            psiElements.flatMapTo(mutableSetOf()) {
                typeTypeParameterNameSet(it)
            }

    private fun typeTypeParameterNameSet(typeOperation: ElixirUnmatchedTypeOperation): Set<String> =
            typeOperation.leftOperand()?.let { typeTypeParameterNameSet(it) } ?: emptySet()
}
