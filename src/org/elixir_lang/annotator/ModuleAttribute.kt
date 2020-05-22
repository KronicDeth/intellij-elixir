package org.elixir_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
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
import org.elixir_lang.reference.ModuleAttribute.Companion.isNonReferencing
import org.elixir_lang.reference.ModuleAttribute.Companion.isSpecificationName
import org.elixir_lang.reference.ModuleAttribute.Companion.isTypeName
import org.elixir_lang.structure_view.element.CallDefinitionHead.Companion.stripAllOuterParentheses
import java.util.*

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
        if (!element.containingFile.viewProvider.languages.contains(Language.INSTANCE)) { // if there exists an EEx tag as a parent, we're in an EEx file
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
                            if (isCallbackName(identifier)) {
                                highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE)
                                highlightCallback(atUnqualifiedNoParenthesesCall, holder)
                            } else if (isDocumentationName(identifier)) {
                                highlight(textRange, holder, ElixirSyntaxHighlighter.DOCUMENTATION_MODULE_ATTRIBUTE)
                                highlightDocumentationText(atUnqualifiedNoParenthesesCall, holder)
                            } else if (isTypeName(identifier)) {
                                highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE)
                                highlightType(atUnqualifiedNoParenthesesCall, holder)
                            } else if (isSpecificationName(identifier)) {
                                highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE)
                                highlightSpecification(atUnqualifiedNoParenthesesCall, holder)
                            } else {
                                highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE)
                            }
                        }

                        private fun visitMaybeUsage(element: AtNonNumericOperation) {
                            val operand: PsiElement? = element.operand()
                            if (operand != null && operand !is ElixirAccessExpression) {
                                visitUsage(element)
                            }
                        }

                        private fun visitUsage(atNonNumericOperation: AtNonNumericOperation) {
                            highlight(
                                    atNonNumericOperation.textRange,
                                    holder,
                                    ElixirSyntaxHighlighter.MODULE_ATTRIBUTE
                            )
                            if (!isNonReferencing(atNonNumericOperation)) {
                                val reference = atNonNumericOperation.reference
                                if (reference != null && reference.resolve() == null) {
                                    holder.createErrorAnnotation(atNonNumericOperation, "Unresolved module attribute")
                                }
                            }
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

    /**
     * Highlights `textRange` with the given `textAttributesKey`.
     *
     * @param textRange         textRange in the document to highlight
     * @param annotationHolder  the container which receives annotations created by the plugin.
     * @param textAttributesKey text attributes to apply to the `node`.
     */
    private fun highlight(textRange: TextRange, annotationHolder: AnnotationHolder, textAttributesKey: TextAttributesKey) {
        annotationHolder.createInfoAnnotation(textRange, null).enforcedTextAttributes = TextAttributes.ERASE_MARKER
        annotationHolder.createInfoAnnotation(textRange, null).enforcedTextAttributes = EditorColorsManager.getInstance().globalScheme.getAttributes(textAttributesKey)
    }

    private fun highlightCallback(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>,
                                  annotationHolder: AnnotationHolder) {
        highlightSpecification(
                atUnqualifiedNoParenthesesCall,
                annotationHolder,
                ElixirSyntaxHighlighter.CALLBACK,
                ElixirSyntaxHighlighter.TYPE
        )
    }

    private fun highlightDocumentationText(
            atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>,
            holder: AnnotationHolder
    ) {
        val noParenthesesOneArgument: PsiElement = atUnqualifiedNoParenthesesCall.noParenthesesOneArgument
        val grandChildren = noParenthesesOneArgument.children
        if (grandChildren.size == 1) {
            val grandChild = grandChildren[0]
            val greatGrandChildren = grandChild.children
            if (greatGrandChildren.size == 1) {
                val greatGrandChild = greatGrandChildren[0]
                if (greatGrandChild is ElixirAtomKeyword) {
                    val atomKeyword = greatGrandChild
                    val text = atomKeyword.text
                    if (text == "false") {
                        holder.createWeakWarningAnnotation(
                                atomKeyword,
                                "Will make documented invisible to the documentation extraction tools like ExDoc.")
                    }
                } else if (greatGrandChild is Heredoc) {
                    val heredoc = greatGrandChild
                    val heredocLineList = heredoc.heredocLineList
                    for (bodied in heredocLineList) {
                        val body = bodied.body
                        highlightFragments(
                                heredoc,
                                body,
                                holder,
                                ElixirSyntaxHighlighter.DOCUMENTATION_TEXT
                        )
                    }
                } else if (greatGrandChild is Line) {
                    val line = greatGrandChild
                    val body = line.body
                    highlightFragments(
                            line,
                            body,
                            holder,
                            ElixirSyntaxHighlighter.DOCUMENTATION_TEXT
                    )
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
     * @param textAttributesKey text attributes to apply to the fragments.
     */
    private fun highlightFragments(fragmented: Fragmented,
                                   body: Body,
                                   annotationHolder: AnnotationHolder,
                                   textAttributesKey: TextAttributesKey) {
        val bodyNode = body.node
        val fragmentNodes = bodyNode.getChildren(
                TokenSet.create(fragmented.fragmentType)
        )
        for (fragmentNode in fragmentNodes) {
            highlight(
                    fragmentNode.textRange,
                    annotationHolder,
                    textAttributesKey
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
                ElixirSyntaxHighlighter.SPECIFICATION,
                ElixirSyntaxHighlighter.TYPE
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
        if (grandChildren.size == 1) {
            val grandChild = grandChildren[0]
            if (grandChild is Match /* Match is invalid.  It will be marked by
                                               MatchOperatorInsteadOfTypeOperator inspection as an error */
                    || grandChild is Type) {
                val infix = grandChild as Infix
                val leftOperand: PsiElement? = infix.leftOperand()
                var typeParameterNameSet: Set<String?> = emptySet<String>()
                if (leftOperand is Call) {
                    val call = leftOperand
                    highlightTypeName(call, annotationHolder)
                    if (call is ElixirMatchedUnqualifiedParenthesesCall) {
                        typeParameterNameSet = highlightTypeLeftOperand(
                                call,
                                annotationHolder
                        )
                    } else if (call !is ElixirMatchedUnqualifiedNoArgumentsCall) {
                        cannotHighlightTypes(call)
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
                }
            } else if (grandChild is ElixirMatchedUnqualifiedParenthesesCall) {
                // seen as `unquote(ast)`, but could also be just the beginning of typing
                val matchedUnqualifiedParenthesesCall = grandChild
                if (Function.UNQUOTE == matchedUnqualifiedParenthesesCall.functionName()) {
                    val secondaryArguments = matchedUnqualifiedParenthesesCall.secondaryArguments()
                    if (secondaryArguments != null) {
                        val typeParameterNameSet = typeTypeParameterNameSet(secondaryArguments)
                        highlightTypesAndTypeTypeParameterDeclarations(
                                secondaryArguments,
                                typeParameterNameSet,
                                annotationHolder,
                                ElixirSyntaxHighlighter.TYPE
                        )
                    }
                } else {
                    highlightTypeName(grandChild, annotationHolder)

                    // Assume it's `@type foo(bar)` before completed as `@type foo(bar) :: bar`
                    highlightTypeLeftOperand(
                            grandChild,
                            annotationHolder
                    )
                }
            } else if (grandChild is QuotableKeywordList) {
                val quotableKeywordPairList = grandChild.quotableKeywordPairList()

                // occurs when user does `my_type: definition` instead of `my_type :: definition`
                if (quotableKeywordPairList.size == 1) {
                    val quotableKeywordPair = quotableKeywordPairList[0]
                    val quotableKeywordKey = quotableKeywordPair.keywordKey
                    if (quotableKeywordKey is ElixirKeywordKey) {
                        highlight(
                                quotableKeywordKey.textRange,
                                annotationHolder,
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
            } else if (grandChild is UnqualifiedNoArgumentsCall<*>) {
                // assume it's a type name that is being typed
                val grandChildCall = grandChild as Call
                val functionNameElement = grandChildCall.functionNameElement()
                if (functionNameElement != null) {
                    highlight(
                            functionNameElement.textRange,
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    )
                }
            } else if (grandChild is UnqualifiedNoParenthesesCall<*>) {
                /* Pretend that `::` separates the functionNameElement from the arguments, so that
                   ```
                   @type coefficient non_neg_integer | :qNaN | :sNaN | :inf
                   ```
                   is retreated like
                   ```
                   @type coefficient :: non_neg_integer | :qNaN | :sNaN | :inf
                   ```
                 */
                val unqualifiedNoParenthesesCall = grandChild
                val functionNameElement = unqualifiedNoParenthesesCall.functionNameElement()
                if (functionNameElement != null) {
                    highlight(
                            functionNameElement.textRange,
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    )
                }
                highlightTypesAndTypeParameterUsages(
                        unqualifiedNoParenthesesCall.noParenthesesOneArgument, emptySet<String>(),
                        annotationHolder,
                        ElixirSyntaxHighlighter.TYPE
                )
            } else {
                cannotHighlightTypes(grandChild)
            }
        }
    }

    private fun highlightTypeError(element: PsiElement,
                                   message: String,
                                   annotationHolder: AnnotationHolder) {
        annotationHolder.createErrorAnnotation(element, message)
    }

    private fun highlightTypeLeftOperand(call: ElixirMatchedUnqualifiedParenthesesCall,
                                         annotationHolder: AnnotationHolder): Set<String?> {
        val primaryArguments = call.primaryArguments()
        val secondaryArguments = call.secondaryArguments()
        val typeParameterNameSet: Set<String?>

        /* if there are secondaryArguments, then it is the type parameters as in
           `@type quote(type_name)(param1, param2) :: {param1, param2}` */if (secondaryArguments != null) {
            typeParameterNameSet = typeTypeParameterNameSet(secondaryArguments)
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
        } else {
            typeParameterNameSet = typeTypeParameterNameSet(primaryArguments)
            highlightTypesAndTypeTypeParameterDeclarations(
                    primaryArguments,
                    typeParameterNameSet,
                    annotationHolder,
                    ElixirSyntaxHighlighter.TYPE
            )
        }
        return typeParameterNameSet
    }

    private fun highlightTypeName(call: Call, annotationHolder: AnnotationHolder) {
        val functionNameElement = call.functionNameElement()
        if (functionNameElement != null) {
            highlight(
                    functionNameElement.textRange,
                    annotationHolder,
                    ElixirSyntaxHighlighter.TYPE
            )
        }
    }

    private fun highlightTypesAndTypeTypeParameterDeclarations(alias: ElixirAlias,
                                                               typeParameterNameSet: Set<String?>,
                                                               annotationHolder: AnnotationHolder,
                                                               typeTextAttributesKey: TextAttributesKey) {
        val name = alias.name
        val textAttributesKey: TextAttributesKey
        textAttributesKey = if (typeParameterNameSet.contains(name)) {
            ElixirSyntaxHighlighter.TYPE_PARAMETER
        } else {
            typeTextAttributesKey
        }
        highlight(alias.textRange, annotationHolder, textAttributesKey)
    }

    private fun highlightTypesAndTypeTypeParameterDeclarations(psiElement: ElixirUnmatchedUnqualifiedNoArgumentsCall,
                                                               typeParameterNameSet: Set<String?>,
                                                               annotationHolder: AnnotationHolder,
                                                               typeTextAttributesKey: TextAttributesKey) {
        val name = psiElement.text
        val textAttributesKey: TextAttributesKey
        textAttributesKey = if (typeParameterNameSet.contains(name)) {
            ElixirSyntaxHighlighter.TYPE_PARAMETER
        } else {
            typeTextAttributesKey
        }
        highlight(psiElement.textRange, annotationHolder, textAttributesKey)
    }

    private fun highlightTypesAndTypeTypeParameterDeclarations(psiElement: PsiElement,
                                                               typeParameterNameSet: Set<String?>,
                                                               annotationHolder: AnnotationHolder,
                                                               typeTextAttributesKey: TextAttributesKey) {
        if (psiElement is ElixirAccessExpression ||
                psiElement is ElixirList ||
                psiElement is ElixirTuple) {
            highlightTypesAndTypeTypeParameterDeclarations(
                    psiElement.children,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is ElixirAlias) {
            highlightTypesAndTypeTypeParameterDeclarations(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is ElixirUnmatchedUnqualifiedNoArgumentsCall) {
            highlightTypesAndTypeTypeParameterDeclarations(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else {
            if (psiElement !is ElixirAtomKeyword) {
                error("Cannot highlight types and type parameter declarations", psiElement)
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

    /**
     * Recursively highlights the types under `atUnqualifiedNoParenthesesCall`.
     *
     * @param leftMostFunctionNameTextAttributesKey      the [ElixirSyntaxHighlighter] [TextAttributesKey] for the
     * name of the callback, type, or function being declared
     * @param leftMostFunctionArgumentsTextAttributesKey the [ElixirSyntaxHighlighter] [TextAttributesKey] for the
     * arguments of the callback, type, or function being declared
     */
    private fun highlightSpecification(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>,
                                       annotationHolder: AnnotationHolder,
                                       leftMostFunctionNameTextAttributesKey: TextAttributesKey,
                                       leftMostFunctionArgumentsTextAttributesKey: TextAttributesKey) {
        val noParenthesesOneArgument: PsiElement = atUnqualifiedNoParenthesesCall.noParenthesesOneArgument
        val grandChildren = noParenthesesOneArgument.children
        if (grandChildren.size == 1) {
            val grandChild = grandChildren[0]
            if (grandChild is Type) {
                val infix = grandChild as Infix
                val leftOperand: PsiElement? = infix.leftOperand()
                if (leftOperand is Call) {
                    val call = leftOperand
                    val functionNameElement = call.functionNameElement()
                    if (functionNameElement != null) {
                        highlight(
                                functionNameElement.textRange,
                                annotationHolder,
                                leftMostFunctionNameTextAttributesKey
                        )
                    }
                    val primaryArguments = call.primaryArguments()
                    if (primaryArguments != null) {
                        highlightTypesAndTypeParameterUsages(
                                primaryArguments, emptySet<String>(),
                                annotationHolder,
                                leftMostFunctionArgumentsTextAttributesKey
                        )
                    }
                    val secondaryArguments = call.secondaryArguments()
                    if (secondaryArguments != null) {
                        highlightTypesAndTypeParameterUsages(
                                secondaryArguments, emptySet<String>(),
                                annotationHolder,
                                leftMostFunctionArgumentsTextAttributesKey
                        )
                    }
                }
                val rightOperand: PsiElement? = infix.rightOperand()
                if (rightOperand != null) {
                    highlightTypesAndTypeParameterUsages(
                            rightOperand, emptySet<String>(),
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    )
                }
            } else if (grandChild is ElixirMatchedWhenOperation) {
                val matchedWhenOperation = grandChild
                val rightOperand: PsiElement? = matchedWhenOperation.rightOperand()
                val typeParameterNameSet: Set<String?>
                typeParameterNameSet = rightOperand?.let { specificationTypeParameterNameSet(it) } ?: emptySet<String>()
                val leftOperand: PsiElement? = matchedWhenOperation.leftOperand()
                if (leftOperand is Type) {
                    val typeOperation = leftOperand
                    val typeOperationLeftOperand: PsiElement? = typeOperation.leftOperand()
                    var strippedTypeOperationLeftOperand: PsiElement? = null
                    if (typeOperationLeftOperand != null) {
                        strippedTypeOperationLeftOperand = stripAllOuterParentheses(typeOperationLeftOperand)
                    }
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
                    val matchedTypeOperationRightOperand: PsiElement? = typeOperation.rightOperand()
                    if (matchedTypeOperationRightOperand != null) {
                        highlightTypesAndTypeParameterUsages(
                                matchedTypeOperationRightOperand,
                                typeParameterNameSet,
                                annotationHolder,
                                ElixirSyntaxHighlighter.TYPE
                        )
                    }
                } else if (leftOperand is Call) {
                    highlightSpecification(
                            leftOperand,
                            annotationHolder,
                            leftMostFunctionNameTextAttributesKey,
                            leftMostFunctionNameTextAttributesKey,
                            typeParameterNameSet
                    )
                } else {
                    cannotHighlightTypes(leftOperand)
                }
                if (rightOperand != null) {
                    highlightTypesAndSpecificationTypeParameterDeclarations(
                            rightOperand,
                            typeParameterNameSet,
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    )
                }
            }
        }
    }

    private fun highlightSpecification(call: Call,
                                       annotationHolder: AnnotationHolder,
                                       leftMostFunctionNameTextAttributesKey: TextAttributesKey,
                                       leftMostFunctionArgumentsTextAttributesKey: TextAttributesKey,
                                       typeParameterNameSet: Set<String?>) {
        val functionNameElement = call.functionNameElement()
        if (functionNameElement != null) {
            highlight(
                    functionNameElement.textRange,
                    annotationHolder,
                    leftMostFunctionNameTextAttributesKey
            )
        }
        val primaryArguments = call.primaryArguments()
        primaryArguments?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, leftMostFunctionArgumentsTextAttributesKey) }
        val secondaryArguments = call.secondaryArguments()
        secondaryArguments?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, leftMostFunctionArgumentsTextAttributesKey) }
    }

    private fun highlightTypesAndSpecificationTypeParameterDeclarations(quotableKeywordPair: QuotableKeywordPair,
                                                                        typeParameterNameSet: Set<String?>,
                                                                        annotationHolder: AnnotationHolder,
                                                                        typeTextAttributesKey: TextAttributesKey) {
        val keywordKey: PsiElement = quotableKeywordPair.keywordKey
        if (typeParameterNameSet.contains(keywordKey.text)) {
            highlight(keywordKey.textRange, annotationHolder, ElixirSyntaxHighlighter.TYPE_PARAMETER)
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
        val leftOperand: PsiElement? = type.leftOperand()
        if (leftOperand != null) {
            if (typeParameterNameSet.contains(leftOperand.text)) {
                highlight(leftOperand.textRange, annotationHolder, ElixirSyntaxHighlighter.TYPE_PARAMETER)
            } else {
                highlightTypesAndTypeParameterUsages(
                        leftOperand,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
            }
        }
        val rightOperand: PsiElement? = type.rightOperand()
        rightOperand?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, typeTextAttributesKey) }
    }

    private fun highlightTypesAndSpecificationTypeParameterDeclarations(psiElement: PsiElement,
                                                                        typeParameterNameSet: Set<String?>,
                                                                        annotationHolder: AnnotationHolder,
                                                                        typeTextAttributesKey: TextAttributesKey) {
        if (psiElement is ElixirAccessExpression ||
                psiElement is ElixirKeywords ||
                psiElement is ElixirList ||
                psiElement is ElixirNoParenthesesKeywords) {
            highlightTypesAndSpecificationTypeParameterDeclarations(
                    psiElement.children,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is QuotableKeywordPair) {
            highlightTypesAndSpecificationTypeParameterDeclarations(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is Type) {
            highlightTypesAndSpecificationTypeParameterDeclarations(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is UnqualifiedNoArgumentsCall<*>) {
            highlightTypesAndSpecificationTypeParameterDeclarations(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is UnqualifiedNoParenthesesCall<*>) {
            highlightTypesAndSpecificationTypeParameterDeclarations(
                    psiElement
            )
        } else {
            error("Cannot highlight types and specification type parameter declarations", psiElement)
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
            annotationHolder: AnnotationHolder,
            textAttributesKey: TextAttributesKey) {
        if (typeParameterNameSet.contains(unqualifiedNoArgumentsCall.functionName())) {
            val functionNameElement = unqualifiedNoArgumentsCall.functionNameElement()
            highlight(
                    functionNameElement.textRange,
                    annotationHolder,
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
                val operator = grandParent.operator()
                if (operator != null) {
                    val rangeOperators = operator.node.getChildren(TokenSet.create(ElixirTypes.RANGE_OPERATOR))
                    if (rangeOperators.size > 0) {
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
        highlightTypeError(decimalFloat, message, annotationHolder)
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
            val stabNoParenthesesSignatures = stabOperation.stabNoParenthesesSignature
            stabNoParenthesesSignatures?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, typeTextAttributesKey) }
        }
        val stabBody = stabOperation.stabBody
        stabBody?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, typeTextAttributesKey) }
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
        val leftOperand: PsiElement? = infix.leftOperand()
        leftOperand?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, typeTextAttributesKey) }
        val rightOperand: PsiElement? = infix.rightOperand()
        rightOperand?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, typeTextAttributesKey) }
    }

    private fun highlightTypesAndTypeParameterUsages(psiElement: PsiElement,
                                                     typeParameterNameSet: Set<String?>,
                                                     annotationHolder: AnnotationHolder,
                                                     typeTextAttributesKey: TextAttributesKey) {
        if (psiElement is Arguments) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is ElixirAccessExpression ||
                psiElement is ElixirAssociationsBase ||
                psiElement is ElixirAssociations ||
                psiElement is ElixirContainerAssociationOperation ||
                psiElement is ElixirKeywordPair ||
                psiElement is ElixirKeywords ||
                psiElement is ElixirList ||
                psiElement is ElixirMapArguments ||
                psiElement is ElixirMapConstructionArguments ||
                psiElement is ElixirNoParenthesesArguments ||
                psiElement is ElixirParentheticalStab ||
                psiElement is ElixirStab ||
                psiElement is ElixirStabBody ||
                psiElement is ElixirStabNoParenthesesSignature ||
                psiElement is ElixirTuple) {
            highlightTypesAndTypeParameterUsages(
                    psiElement.children,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is ElixirDecimalFloat) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    annotationHolder
            )
        } else if (psiElement is ElixirMapOperation) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is ElixirMapUpdateArguments) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is ElixirRelativeIdentifier ||
                psiElement is UnqualifiedNoArgumentsCall<*>) {
            // highlight entire element
            val name = psiElement.text
            val textAttributesKey: TextAttributesKey
            textAttributesKey = if (typeParameterNameSet.contains(name)) {
                ElixirSyntaxHighlighter.TYPE_PARAMETER
            } else {
                typeTextAttributesKey
            }
            highlight(psiElement.textRange, annotationHolder, textAttributesKey)
        } else if (psiElement is ElixirStabParenthesesSignature) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is ElixirStabOperation) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is ElixirStructOperation) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is InterpolatedString) {
            highlightTypeError(psiElement, "Strings aren't allowed in types", annotationHolder)
        } else if (psiElement is Infix && psiElement !is When) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is QualifiedParenthesesCall<*>) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is QualifiedAlias) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is QualifiedNoArgumentsCall<*>) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is QualifiedNoParenthesesCall<*>) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is UnqualifiedNoParenthesesCall<*>) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (psiElement is UnqualifiedParenthesesCall<*>) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            )
        } else if (!( /* Occurs in the case of typing a {@code @type name ::} above a {@code @doc <HEREDOC>} and the
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

    private fun highlightTypesAndTypeParameterUsages(
            psiElements: Array<PsiElement>,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            textAttributesKey: TextAttributesKey) {
        for (psiElement in psiElements) {
            psiElement?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, textAttributesKey) }
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
        val secondaryArguments = qualifiedParenthesesCall.secondaryArguments()
        secondaryArguments?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, textAttributesKey) }
    }

    private fun highlightTypesAndTypeParameterUsages(
            unqualifiedNoParenthesesCall: UnqualifiedNoParenthesesCall<*>,
            typeParameterNameSet: Set<String?>,
            annotationHolder: AnnotationHolder,
            typeTextAttributesKey: TextAttributesKey) {
        val functionNameElement = unqualifiedNoParenthesesCall.functionNameElement()
        if (functionNameElement != null) {
            highlight(functionNameElement.textRange, annotationHolder, typeTextAttributesKey)
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
                highlight(functionNameElement.textRange, annotationHolder, typeTextAttributesKey)
                highlightTypesAndTypeParameterUsages(
                        unqualifiedParenthesesCall.primaryArguments(),
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                )
                val secondaryArguments = unqualifiedParenthesesCall.secondaryArguments()
                secondaryArguments?.let { highlightTypesAndTypeParameterUsages(it, typeParameterNameSet, annotationHolder, typeTextAttributesKey) }
            } else {
                error("Cannot highlight types and type parameter usages", unqualifiedParenthesesCall)
            }
        }
    }

    private fun specificationTypeParameterNameSet(keywordPair: ElixirKeywordPair): Set<String?> {
        return setOf(keywordPair.keywordKey.text)
    }

    private fun specificationTypeParameterNameSet(noParenthesesKeywordPair: ElixirNoParenthesesKeywordPair): Set<String?> {
        return setOf(noParenthesesKeywordPair.keywordKey.text)
    }

    /**
     * A type operator is an error, keyword pairs should be used for `when type: definition` for expression-local types,
     * but using `::` is a common error, so support it.
     */
    private fun specificationTypeParameterNameSet(type: Type): Set<String?> {
        val leftOperand: PsiElement? = type.leftOperand()
        val typeParameterNameSet: Set<String?>
        typeParameterNameSet = if (leftOperand != null) {
            setOf(leftOperand.text)
        } else {
            error("Type does not have a left operand", type)
            emptySet<String>()
        }
        return typeParameterNameSet
    }

    private fun specificationTypeParameterNameSet(psiElement: PsiElement): Set<String?> {
        val parameterNameSet: Set<String?>
        parameterNameSet = if (psiElement is ElixirAccessExpression ||
                psiElement is ElixirKeywords ||
                psiElement is ElixirList ||
                psiElement is ElixirNoParenthesesKeywords) {
            specificationTypeParameterNameSet(psiElement.children)
        } else if (psiElement is ElixirKeywordPair) {
            specificationTypeParameterNameSet(psiElement)
        } else if (psiElement is Type) {
            specificationTypeParameterNameSet(psiElement)
        } else if (psiElement is ElixirNoParenthesesKeywordPair) {
            specificationTypeParameterNameSet(psiElement)
        } else if (psiElement is UnqualifiedNoArgumentsCall<*>) {
            specificationTypeParameterNameSet(psiElement)
        } else if (psiElement is UnqualifiedNoParenthesesCall<*>) {
            specificationTypeParameterNameSet(psiElement)
        } else {
            error("Cannot extract specification type parameter name set", psiElement)
            emptySet<String>()
        }
        return parameterNameSet
    }

    private fun specificationTypeParameterNameSet(psiElements: Array<PsiElement>): Set<String?> {
        val accumulatedTypeParameterNameSet: MutableSet<String?> = HashSet()
        for (psiElement in psiElements) {
            accumulatedTypeParameterNameSet.addAll(specificationTypeParameterNameSet(psiElement))
        }
        return accumulatedTypeParameterNameSet
    }

    /**
     * Occurs temporarily while typing before `:` in KeywordPairs after the `when`, such as in
     * `@spec foo(id) :: id when id` before finishing typing `@spec foo(id) :: id when id: String.t`.
     */
    private fun specificationTypeParameterNameSet(unqualifiedNoArgumentsCall: UnqualifiedNoArgumentsCall<*>): Set<String?> {
        val name = unqualifiedNoArgumentsCall.functionName()
        val nameSet: Set<String?>
        nameSet = if (name != null) {
            setOf(unqualifiedNoArgumentsCall.functionName())
        } else {
            emptySet<String>()
        }
        return nameSet
    }

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
     * Assume bare aliases are incorrectly capitalized type parameters, say from someone's that used to generics
     * in Java.
     *
     * See https://github.com/KronicDeth/intellij-elixir/issues/694
     */
    private fun typeTypeParameterNameSet(alias: ElixirAlias): Set<String> {
        return setOf(alias.name)
    }

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

    private fun typeTypeParameterNameSet(psiElement: PsiElement): Set<String?> {
        val typeParameterNameSet: Set<String?>
        typeParameterNameSet = if (psiElement is ElixirAccessExpression) {
            typeTypeParameterNameSet(psiElement.getChildren())
        } else if (psiElement is ElixirAlias) {
            /* Assume bare aliases are incorrectly capitalized type parameters, say from someone's that used to generics
               in Java.

               See https://github.com/KronicDeth/intellij-elixir/issues/694 */
            typeTypeParameterNameSet(psiElement)
        } else if (psiElement is ElixirTuple) {
            typeTypeParameterNameSet(psiElement)
        } else if (psiElement is ElixirUnmatchedUnqualifiedNoArgumentsCall) {
            setOf(psiElement.getText())
        } else {
            error("Cannot extract type type parameter name set", psiElement)
            emptySet<String>()
        }
        return typeParameterNameSet
    }

    private fun typeTypeParameterNameSet(psiElements: Array<PsiElement>): Set<String?> {
        val typeParameterNameSet: MutableSet<String?> = HashSet()
        for (psiElement in psiElements) {
            typeParameterNameSet.addAll(typeTypeParameterNameSet(psiElement))
        }
        return typeParameterNameSet
    }
}
