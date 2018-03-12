package org.elixir_lang.annotator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.ElixirSyntaxHighlighter;
import org.elixir_lang.errorreport.Logger;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.operation.*;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.elixir_lang.psi.ElixirTypes.RANGE_OPERATOR;
import static org.elixir_lang.psi.call.name.Function.UNQUOTE;
import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.psi.impl.ElixirAtIdentifierImplKt.identifierName;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.stripAccessExpression;
import static org.elixir_lang.reference.ModuleAttribute.*;
import static org.elixir_lang.structure_view.element.CallDefinitionHead.stripAllOuterParentheses;

/**
 * Annotates module attributes.
 */
public class ModuleAttribute implements Annotator, DumbAware {
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
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull final AnnotationHolder holder) {
        element.accept(
                new PsiRecursiveElementVisitor() {
                    /*
                     *
                     * Instance Methods
                     *
                     */

                    /*
                     * Public Instance Methods
                     */

                    @Override
                    public void visitElement(@NotNull final PsiElement element) {
                        if (element instanceof AtNonNumericOperation) {
                            visitMaybeUsage((AtNonNumericOperation) element);
                        } else if (element instanceof AtUnqualifiedNoParenthesesCall) {
                            visitDeclaration((AtUnqualifiedNoParenthesesCall) element);
                        }
                    }

                    /*
                     * Private Instance Methods
                     */

                    private void visitDeclaration(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
                        ElixirAtIdentifier atIdentifier = atUnqualifiedNoParenthesesCall.getAtIdentifier();
                        TextRange textRange = atIdentifier.getTextRange();

                        String identifier = identifierName(atIdentifier);

                        if (isCallbackName(identifier)) {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);

                            highlightCallback(atUnqualifiedNoParenthesesCall, holder);
                        } else if (isDocumentationName(identifier)) {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.DOCUMENTATION_MODULE_ATTRIBUTE);

                            highlightDocumentationText(atUnqualifiedNoParenthesesCall, holder);
                        } else if (isTypeName(identifier)) {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);

                            highlightType(atUnqualifiedNoParenthesesCall, holder);
                        } else if (isSpecificationName(identifier)) {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);

                            highlightSpecification(atUnqualifiedNoParenthesesCall, holder);
                        } else {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);
                        }
                    }


                    private void visitMaybeUsage(@NotNull final AtNonNumericOperation element) {
                        PsiElement operand = element.operand();

                        if (operand != null && !(operand instanceof ElixirAccessExpression)) {
                            visitUsage(element);
                        }
                    }

                    private void visitUsage(@NotNull final AtNonNumericOperation atNonNumericOperation) {
                        highlight(
                                atNonNumericOperation.getTextRange(),
                                holder,
                                ElixirSyntaxHighlighter.MODULE_ATTRIBUTE
                        );

                        if (!isNonReferencing(atNonNumericOperation)) {
                            PsiReference reference = atNonNumericOperation.getReference();

                            if (reference != null && reference.resolve() == null) {
                                holder.createErrorAnnotation(atNonNumericOperation, "Unresolved module attribute");
                            }
                        }
                    }
                }
        );
    }

    /*
     * Private Instance Methods
     */

    private void cannotHighlightTypes(PsiElement element) {
        error("Cannot highlight types", element);
    }

    private void error(@NotNull String userMessage, PsiElement element) {
        Logger.error(this.getClass(), userMessage, element);
    }

    /**
     * Highlights `textRange` with the given `textAttributesKey`.
     *
     * @param textRange         textRange in the document to highlight
     * @param annotationHolder  the container which receives annotations created by the plugin.
     * @param textAttributesKey text attributes to apply to the `node`.
     */
    private void highlight(@NotNull final TextRange textRange, @NotNull AnnotationHolder annotationHolder, @NotNull final TextAttributesKey textAttributesKey) {
        annotationHolder.createInfoAnnotation(textRange, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
        annotationHolder.createInfoAnnotation(textRange, null).setEnforcedTextAttributes(EditorColorsManager.getInstance().getGlobalScheme().getAttributes(textAttributesKey));
    }

    private void highlightCallback(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall,
                                   @NotNull final AnnotationHolder annotationHolder) {
        highlightSpecification(
                atUnqualifiedNoParenthesesCall,
                annotationHolder,
                ElixirSyntaxHighlighter.CALLBACK,
                ElixirSyntaxHighlighter.TYPE
        );
    }

    private void highlightDocumentationText(
            @NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall,
            @NotNull final AnnotationHolder holder
    ) {
        PsiElement noParenthesesOneArgument = atUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument();
        PsiElement[] grandChildren = noParenthesesOneArgument.getChildren();

        if (grandChildren.length == 1) {
            PsiElement grandChild = grandChildren[0];

            PsiElement[] greatGrandChildren = grandChild.getChildren();

            if (greatGrandChildren.length == 1) {
                PsiElement greatGrandChild = greatGrandChildren[0];

                if (greatGrandChild instanceof ElixirAtomKeyword) {
                    ElixirAtomKeyword atomKeyword = (ElixirAtomKeyword) greatGrandChild;
                    String text = atomKeyword.getText();

                    if (text.equals("false")) {
                        holder.createWeakWarningAnnotation(
                                atomKeyword,
                                "Will make documented invisible to the documentation extraction tools like ExDoc.");
                    }
                } else if (greatGrandChild instanceof Heredoc) {
                    Heredoc heredoc = (Heredoc) greatGrandChild;
                    List<HeredocLine> heredocLineList = heredoc.getHeredocLineList();

                    for (Bodied bodied : heredocLineList) {
                        Body body = bodied.getBody();

                        highlightFragments(
                                heredoc,
                                body,
                                holder,
                                ElixirSyntaxHighlighter.DOCUMENTATION_TEXT
                        );
                    }
                } else if (greatGrandChild instanceof Line) {
                    Line line = (Line) greatGrandChild;
                    Body body = line.getBody();

                    highlightFragments(
                            line,
                            body,
                            holder,
                            ElixirSyntaxHighlighter.DOCUMENTATION_TEXT
                    );
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
    private void highlightFragments(@NotNull final Fragmented fragmented,
                                    @NotNull final Body body,
                                    @NotNull AnnotationHolder annotationHolder,
                                    @NotNull final TextAttributesKey textAttributesKey) {
        ASTNode bodyNode = body.getNode();
        ASTNode[] fragmentNodes = bodyNode.getChildren(
                TokenSet.create(fragmented.getFragmentType())
        );

        for (ASTNode fragmentNode : fragmentNodes) {
            highlight(
                    fragmentNode.getTextRange(),
                    annotationHolder,
                    textAttributesKey
            );
        }
    }

    /**
     * Highlights the function call name as a `ElixirSyntaxHighlighter.SPECIFICATION
     */
    private void highlightSpecification(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall,
                                        @NotNull final AnnotationHolder annotationHolder) {
        highlightSpecification(
                atUnqualifiedNoParenthesesCall,
                annotationHolder,
                ElixirSyntaxHighlighter.SPECIFICATION,
                ElixirSyntaxHighlighter.TYPE
        );
    }

    /**
     * Highlights the function name of the declared @type, @typep, or @opaque as an {@link ElixirSyntaxHighlighter#TYPE}
     * and the its parameters as {@link ElixirSyntaxHighlighter#TYPE_PARAMETER}.
     */
    private void highlightType(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall,
                               @NotNull final AnnotationHolder annotationHolder) {
        PsiElement noParenthesesOneArgument = atUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument();
        PsiElement[] grandChildren = noParenthesesOneArgument.getChildren();

        if (grandChildren.length == 1) {
            PsiElement grandChild = grandChildren[0];

            if (grandChild instanceof Match /* Match is invalid.  It will be marked by
                                               MatchOperatorInsteadOfTypeOperator inspection as an error */
                    || grandChild instanceof Type) {
                Infix infix = (Infix) grandChild;
                PsiElement leftOperand = infix.leftOperand();
                Set<String> typeParameterNameSet = Collections.emptySet();

                if (leftOperand instanceof Call) {
                    Call call = (Call) leftOperand;

                    highlightTypeName(call, annotationHolder);

                    if (call instanceof ElixirMatchedUnqualifiedParenthesesCall) {
                        typeParameterNameSet = highlightTypeLeftOperand(
                                (ElixirMatchedUnqualifiedParenthesesCall) call,
                                annotationHolder
                        );
                    } else if (!(call instanceof ElixirMatchedUnqualifiedNoArgumentsCall)) {
                        cannotHighlightTypes(call);
                    }
                } else {
                    cannotHighlightTypes(leftOperand);
                }

                PsiElement rightOperand = infix.rightOperand();

                if (rightOperand != null) {
                    highlightTypesAndTypeParameterUsages(
                            rightOperand,
                            typeParameterNameSet,
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    );
                }
            } else if (grandChild instanceof ElixirMatchedUnqualifiedParenthesesCall) {
                // seen as `unquote(ast)`, but could also be just the beginning of typing
                ElixirMatchedUnqualifiedParenthesesCall matchedUnqualifiedParenthesesCall = (ElixirMatchedUnqualifiedParenthesesCall) grandChild;

                if (UNQUOTE.equals(matchedUnqualifiedParenthesesCall.functionName())) {
                    PsiElement[] secondaryArguments = matchedUnqualifiedParenthesesCall.secondaryArguments();

                    if (secondaryArguments != null) {
                        Set<String> typeParameterNameSet = typeTypeParameterNameSet(secondaryArguments);

                        highlightTypesAndTypeTypeParameterDeclarations(
                                secondaryArguments,
                                typeParameterNameSet,
                                annotationHolder,
                                ElixirSyntaxHighlighter.TYPE
                        );
                    }
                } else {
                    ElixirMatchedUnqualifiedParenthesesCall grandChildCall = (ElixirMatchedUnqualifiedParenthesesCall) grandChild;

                    highlightTypeName(grandChildCall, annotationHolder);

                    // Assume it's `@type foo(bar)` before completed as `@type foo(bar) :: bar`
                    highlightTypeLeftOperand(
                            (ElixirMatchedUnqualifiedParenthesesCall) grandChild,
                            annotationHolder
                    );
                }
            } else if (grandChild instanceof QuotableKeywordList) {
                QuotableKeywordList quotableKeywordList = (QuotableKeywordList) grandChild;
                List<QuotableKeywordPair> quotableKeywordPairList = quotableKeywordList.quotableKeywordPairList();

                // occurs when user does `my_type: definition` instead of `my_type :: definition`
                if (quotableKeywordPairList.size() == 1) {
                    QuotableKeywordPair quotableKeywordPair = quotableKeywordPairList.get(0);

                    Quotable quotableKeywordKey = quotableKeywordPair.getKeywordKey();

                    if (quotableKeywordKey instanceof ElixirKeywordKey) {
                        ElixirKeywordKey keywordKey = (ElixirKeywordKey) quotableKeywordKey;

                        highlight(
                                keywordKey.getTextRange(),
                                annotationHolder,
                                ElixirSyntaxHighlighter.TYPE
                        );
                    }

                    Quotable quotableKeywordValue = quotableKeywordPair.getKeywordValue();

                    highlightTypesAndTypeParameterUsages(
                            quotableKeywordValue,
                            Collections.emptySet(),
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    );
                }
                // Otherwise, allow the normal, non-type highlighting
            } else if (grandChild instanceof UnqualifiedNoArgumentsCall) {
                // assume it's a type name that is being typed
                Call grandChildCall = (Call) grandChild;
                PsiElement functionNameElement = grandChildCall.functionNameElement();

                if (functionNameElement != null) {
                    highlight(
                            functionNameElement.getTextRange(),
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    );
                }
            } else if (grandChild instanceof UnqualifiedNoParenthesesCall) {
                /* Pretend that `::` separates the functionNameElement from the arguments, so that
                   ```
                   @type coefficient non_neg_integer | :qNaN | :sNaN | :inf
                   ```
                   is retreated like
                   ```
                   @type coefficient :: non_neg_integer | :qNaN | :sNaN | :inf
                   ```
                 */
                UnqualifiedNoParenthesesCall unqualifiedNoParenthesesCall = (UnqualifiedNoParenthesesCall) grandChild;

                PsiElement functionNameElement = unqualifiedNoParenthesesCall.functionNameElement();

                if (functionNameElement != null) {
                    highlight(
                            functionNameElement.getTextRange(),
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    );
                }

                highlightTypesAndTypeParameterUsages(
                        unqualifiedNoParenthesesCall.getNoParenthesesOneArgument(),
                        Collections.emptySet(),
                        annotationHolder,
                        ElixirSyntaxHighlighter.TYPE
                );
            } else {
                cannotHighlightTypes(grandChild);
            }
        }
    }

    private void highlightTypeError(@NotNull PsiElement element,
                                    @NotNull String message,
                                    @NotNull AnnotationHolder annotationHolder) {
        annotationHolder.createErrorAnnotation(element, message);
    }

    private Set<String> highlightTypeLeftOperand(@NotNull final ElixirMatchedUnqualifiedParenthesesCall call,
                                                 @NotNull final AnnotationHolder annotationHolder) {
        PsiElement[] primaryArguments = call.primaryArguments();
        PsiElement[] secondaryArguments = call.secondaryArguments();
        Set<String> typeParameterNameSet;

        /* if there are secondaryArguments, then it is the type parameters as in
           `@type quote(type_name)(param1, param2) :: {param1, param2}` */
        if (secondaryArguments != null) {
            typeParameterNameSet = typeTypeParameterNameSet(secondaryArguments);

            highlightTypesAndTypeParameterUsages(
                    primaryArguments,
                    /* as stated above, if there are secondary arguments, then the primary arguments are
                       to quote or some equivalent metaprogramming. */
                    Collections.emptySet(),
                    annotationHolder,
                    ElixirSyntaxHighlighter.TYPE
            );

            highlightTypesAndTypeTypeParameterDeclarations(
                    secondaryArguments,
                    typeParameterNameSet,
                    annotationHolder,
                    ElixirSyntaxHighlighter.TYPE
            );
        } else {
            typeParameterNameSet = typeTypeParameterNameSet(primaryArguments);

            highlightTypesAndTypeTypeParameterDeclarations(
                    primaryArguments,
                    typeParameterNameSet,
                    annotationHolder,
                    ElixirSyntaxHighlighter.TYPE
            );
        }

        return typeParameterNameSet;
    }

    private void highlightTypeName(@NotNull final Call call, @NotNull AnnotationHolder annotationHolder) {
        PsiElement functionNameElement = call.functionNameElement();

        if (functionNameElement != null) {
            highlight(
                    functionNameElement.getTextRange(),
                    annotationHolder,
                    ElixirSyntaxHighlighter.TYPE
            );
        }
    }


    private void highlightTypesAndTypeTypeParameterDeclarations(ElixirAlias alias,
                                                                Set<String> typeParameterNameSet,
                                                                AnnotationHolder annotationHolder,
                                                                TextAttributesKey typeTextAttributesKey) {
        String name = alias.getName();
        TextAttributesKey textAttributesKey;

        if (typeParameterNameSet.contains(name)) {
            textAttributesKey = ElixirSyntaxHighlighter.TYPE_PARAMETER;
        } else {
            textAttributesKey = typeTextAttributesKey;
        }

        highlight(alias.getTextRange(), annotationHolder, textAttributesKey);
    }

    private void highlightTypesAndTypeTypeParameterDeclarations(ElixirUnmatchedUnqualifiedNoArgumentsCall psiElement,
                                                                Set<String> typeParameterNameSet,
                                                                AnnotationHolder annotationHolder,
                                                                TextAttributesKey typeTextAttributesKey) {
        String name = psiElement.getText();
        TextAttributesKey textAttributesKey;

        if (typeParameterNameSet.contains(name)) {
            textAttributesKey = ElixirSyntaxHighlighter.TYPE_PARAMETER;
        } else {
            textAttributesKey = typeTextAttributesKey;
        }

        highlight(psiElement.getTextRange(), annotationHolder, textAttributesKey);
    }

    private void highlightTypesAndTypeTypeParameterDeclarations(PsiElement psiElement,
                                                                Set<String> typeParameterNameSet,
                                                                AnnotationHolder annotationHolder,
                                                                TextAttributesKey typeTextAttributesKey) {
        if (psiElement instanceof ElixirAccessExpression ||
                psiElement instanceof ElixirList ||
                psiElement instanceof ElixirTuple) {
            highlightTypesAndTypeTypeParameterDeclarations(
                    psiElement.getChildren(),
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof ElixirAlias) {
            highlightTypesAndTypeTypeParameterDeclarations(
                    (ElixirAlias) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof ElixirUnmatchedUnqualifiedNoArgumentsCall) {
            highlightTypesAndTypeTypeParameterDeclarations(
                    (ElixirUnmatchedUnqualifiedNoArgumentsCall) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else {
            if (!(psiElement instanceof ElixirAtomKeyword)) {
                error("Cannot highlight types and type parameter declarations", psiElement);
            }
        }
    }

    private void highlightTypesAndTypeTypeParameterDeclarations(
            PsiElement[] psiElements,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey typeTextAttributesKey) {
        for (PsiElement psiElement : psiElements) {
            highlightTypesAndTypeTypeParameterDeclarations(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        }
    }

    /**
     * Recursively highlights the types under `atUnqualifiedNoParenthesesCall`.
     *
     * @param leftMostFunctionNameTextAttributesKey      the {@link ElixirSyntaxHighlighter} {@link TextAttributesKey} for the
     *                                                   name of the callback, type, or function being declared
     * @param leftMostFunctionArgumentsTextAttributesKey the {@link ElixirSyntaxHighlighter} {@link TextAttributesKey} for the
     *                                                   arguments of the callback, type, or function being declared
     */
    private void highlightSpecification(AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall,
                                        AnnotationHolder annotationHolder,
                                        TextAttributesKey leftMostFunctionNameTextAttributesKey,
                                        TextAttributesKey leftMostFunctionArgumentsTextAttributesKey) {
        PsiElement noParenthesesOneArgument = atUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument();
        PsiElement[] grandChildren = noParenthesesOneArgument.getChildren();

        if (grandChildren.length == 1) {
            PsiElement grandChild = grandChildren[0];

            if (grandChild instanceof Type) {
                Infix infix = (Infix) grandChild;
                PsiElement leftOperand = infix.leftOperand();

                if (leftOperand instanceof Call) {
                    Call call = (Call) leftOperand;
                    PsiElement functionNameElement = call.functionNameElement();

                    if (functionNameElement != null) {
                        highlight(
                                functionNameElement.getTextRange(),
                                annotationHolder,
                                leftMostFunctionNameTextAttributesKey
                        );
                    }

                    PsiElement[] primaryArguments = call.primaryArguments();

                    if (primaryArguments != null) {
                        highlightTypesAndTypeParameterUsages(
                                primaryArguments,
                                Collections.emptySet(),
                                annotationHolder,
                                leftMostFunctionArgumentsTextAttributesKey
                        );
                    }

                    PsiElement[] secondaryArguments = call.secondaryArguments();

                    if (secondaryArguments != null) {
                        highlightTypesAndTypeParameterUsages(
                                secondaryArguments,
                                Collections.emptySet(),
                                annotationHolder,
                                leftMostFunctionArgumentsTextAttributesKey
                        );
                    }
                }

                PsiElement rightOperand = infix.rightOperand();

                if (rightOperand != null) {
                    highlightTypesAndTypeParameterUsages(
                            rightOperand,
                            Collections.emptySet(),
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    );
                }
            } else if (grandChild instanceof ElixirMatchedWhenOperation) {
                ElixirMatchedWhenOperation matchedWhenOperation = (ElixirMatchedWhenOperation) grandChild;
                PsiElement rightOperand = matchedWhenOperation.rightOperand();
                Set<String> typeParameterNameSet;

                if (rightOperand != null) {
                    typeParameterNameSet = specificationTypeParameterNameSet(rightOperand);
                } else {
                    typeParameterNameSet = Collections.emptySet();
                }

                PsiElement leftOperand = matchedWhenOperation.leftOperand();

                if (leftOperand instanceof Type) {
                    Type typeOperation = (Type) leftOperand;
                    PsiElement typeOperationLeftOperand = typeOperation.leftOperand();
                    PsiElement strippedTypeOperationLeftOperand = null;

                    if (typeOperationLeftOperand != null) {
                        strippedTypeOperationLeftOperand = stripAllOuterParentheses(typeOperationLeftOperand);
                    }

                    if (strippedTypeOperationLeftOperand instanceof Call) {
                        highlightSpecification(
                                (Call) strippedTypeOperationLeftOperand,
                                annotationHolder,
                                leftMostFunctionNameTextAttributesKey,
                                leftMostFunctionNameTextAttributesKey,
                                typeParameterNameSet
                        );
                    } else {
                        cannotHighlightTypes(strippedTypeOperationLeftOperand);
                    }

                    PsiElement matchedTypeOperationRightOperand = typeOperation.rightOperand();

                    if (matchedTypeOperationRightOperand != null) {
                        highlightTypesAndTypeParameterUsages(
                                matchedTypeOperationRightOperand,
                                typeParameterNameSet,
                                annotationHolder,
                                ElixirSyntaxHighlighter.TYPE
                        );
                    }
                } else if (leftOperand instanceof Call) {
                    highlightSpecification(
                            (Call) leftOperand,
                            annotationHolder,
                            leftMostFunctionNameTextAttributesKey,
                            leftMostFunctionNameTextAttributesKey,
                            typeParameterNameSet
                    );
                } else {
                    cannotHighlightTypes(leftOperand);
                }

                if (rightOperand != null) {
                    highlightTypesAndSpecificationTypeParameterDeclarations(
                            rightOperand,
                            typeParameterNameSet,
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    );
                }
            }
        }
    }

    private void highlightSpecification(@NotNull Call call,
                                        AnnotationHolder annotationHolder,
                                        TextAttributesKey leftMostFunctionNameTextAttributesKey,
                                        TextAttributesKey leftMostFunctionArgumentsTextAttributesKey,
                                        Set<String> typeParameterNameSet) {
        PsiElement functionNameElement = call.functionNameElement();

        if (functionNameElement != null) {
            highlight(
                    functionNameElement.getTextRange(),
                    annotationHolder,
                    leftMostFunctionNameTextAttributesKey
            );
        }

        PsiElement[] primaryArguments = call.primaryArguments();

        if (primaryArguments != null) {
            highlightTypesAndTypeParameterUsages(
                    primaryArguments,
                    typeParameterNameSet,
                    annotationHolder,
                    leftMostFunctionArgumentsTextAttributesKey
            );
        }

        PsiElement[] secondaryArguments = call.secondaryArguments();

        if (secondaryArguments != null) {
            highlightTypesAndTypeParameterUsages(
                    secondaryArguments,
                    typeParameterNameSet,
                    annotationHolder,
                    leftMostFunctionArgumentsTextAttributesKey
            );
        }
    }

    private void highlightTypesAndSpecificationTypeParameterDeclarations(QuotableKeywordPair quotableKeywordPair,
                                                                         Set<String> typeParameterNameSet,
                                                                         AnnotationHolder annotationHolder,
                                                                         TextAttributesKey typeTextAttributesKey) {
        PsiElement keywordKey = quotableKeywordPair.getKeywordKey();

        if (typeParameterNameSet.contains(keywordKey.getText())) {
            highlight(keywordKey.getTextRange(), annotationHolder, ElixirSyntaxHighlighter.TYPE_PARAMETER);
        } else {
            highlightTypesAndTypeParameterUsages(
                    keywordKey,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        }

        highlightTypesAndTypeParameterUsages(
                quotableKeywordPair.getKeywordValue(),
                typeParameterNameSet,
                annotationHolder,
                typeTextAttributesKey
        );
    }

    /**
     * `::` is an error, but can happen due to user error either because they think `::` should work in the `when`
     * clause or when overtyping the `:` for the proper keyword list used in `when`.
     */
    private void highlightTypesAndSpecificationTypeParameterDeclarations(@NotNull Type type,
                                                                         Set<String> typeParameterNameSet,
                                                                         AnnotationHolder annotationHolder,
                                                                         TextAttributesKey typeTextAttributesKey) {
        PsiElement leftOperand = type.leftOperand();

        if (leftOperand != null) {
            if (typeParameterNameSet.contains(leftOperand.getText())) {
                highlight(leftOperand.getTextRange(), annotationHolder, ElixirSyntaxHighlighter.TYPE_PARAMETER);
            } else {
                highlightTypesAndTypeParameterUsages(
                        leftOperand,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                );
            }
        }

        PsiElement rightOperand = type.rightOperand();

        if (rightOperand != null) {
            highlightTypesAndTypeParameterUsages(
                    rightOperand,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        }
    }

    private void highlightTypesAndSpecificationTypeParameterDeclarations(@NotNull PsiElement psiElement,
                                                                         Set<String> typeParameterNameSet,
                                                                         AnnotationHolder annotationHolder,
                                                                         TextAttributesKey typeTextAttributesKey) {
        if (psiElement instanceof ElixirAccessExpression ||
                psiElement instanceof ElixirKeywords ||
                psiElement instanceof ElixirList ||
                psiElement instanceof ElixirNoParenthesesKeywords) {
            highlightTypesAndSpecificationTypeParameterDeclarations(
                    psiElement.getChildren(),
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof QuotableKeywordPair) {
            highlightTypesAndSpecificationTypeParameterDeclarations(
                    (QuotableKeywordPair) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof Type) {
            highlightTypesAndSpecificationTypeParameterDeclarations(
                    (Type) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof UnqualifiedNoArgumentsCall) {
            highlightTypesAndSpecificationTypeParameterDeclarations(
                    (UnqualifiedNoArgumentsCall) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof UnqualifiedNoParenthesesCall) {
            highlightTypesAndSpecificationTypeParameterDeclarations(
                    (UnqualifiedNoParenthesesCall) psiElement
            );
        } else {
            error("Cannot highlight types and specification type parameter declarations", psiElement);
        }
    }

    private void highlightTypesAndSpecificationTypeParameterDeclarations(PsiElement[] psiElements,
                                                                         Set<String> typeParameterNameSet,
                                                                         AnnotationHolder annotationHolder,
                                                                         TextAttributesKey typeTextAttributesKey) {
        for (PsiElement psiElement : psiElements) {
            highlightTypesAndSpecificationTypeParameterDeclarations(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        }
    }

    private void highlightTypesAndSpecificationTypeParameterDeclarations(
            UnqualifiedNoArgumentsCall unqualifiedNoArgumentsCall,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            @SuppressWarnings("unused") TextAttributesKey textAttributesKey) {
        if (typeParameterNameSet.contains(unqualifiedNoArgumentsCall.functionName())) {
            PsiElement functionNameElement = unqualifiedNoArgumentsCall.functionNameElement();

            highlight(
                    functionNameElement.getTextRange(),
                    annotationHolder,
                    ElixirSyntaxHighlighter.TYPE_PARAMETER
            );
        }
    }

    private void highlightTypesAndSpecificationTypeParameterDeclarations(
            UnqualifiedNoParenthesesCall unqualifiedNoParenthesesCall) {
        if (!CallDefinitionClause.is(unqualifiedNoParenthesesCall)) {
            error(
                    "Cannot highlight types and specification type parameter declarations in UnqualifiedNoParenthesesCall that is not a call definition clause", unqualifiedNoParenthesesCall);
        }
    }

    private void highlightTypesAndTypeParameterUsages(Arguments arguments,
                                                      Set<String> typeParameterNameSet,
                                                      AnnotationHolder annotationHolder,
                                                      TextAttributesKey textAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                arguments.arguments(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        );
    }

    private void highlightTypesAndTypeParameterUsages(@NotNull ElixirDecimalFloat decimalFloat,
                                                      @NotNull AnnotationHolder annotationHolder) {
        PsiElement parent = decimalFloat.getParent();
        String message = null;

        if (parent instanceof ElixirAccessExpression) {
            PsiElement grandParent = parent.getParent();

            if (grandParent instanceof Two) {
                Two two = (Two) grandParent;
                Operator operator = two.operator();

                if (operator != null) {
                    ASTNode[] rangeOperators = operator.getNode().getChildren(TokenSet.create(RANGE_OPERATOR));

                    if (rangeOperators.length > 0) {
                        message = "Floats aren't allowed in Ranges";
                    }
                }
            }
        } else {
            cannotHighlightTypes(decimalFloat);
        }

        if (message == null) {
            message = "Float literals are not allowed in types: use float() instead";
        }

        highlightTypeError(decimalFloat, message, annotationHolder);
    }

    private void highlightTypesAndTypeParameterUsages(ElixirMapOperation mapOperation,
                                                      Set<String> typeParameterNameSet,
                                                      AnnotationHolder annotationHolder,
                                                      TextAttributesKey typeTextAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                mapOperation.getMapArguments(),
                typeParameterNameSet,
                annotationHolder,
                typeTextAttributesKey
        );
    }

    private void highlightTypesAndTypeParameterUsages(@NotNull ElixirMapUpdateArguments mapUpdateArguments,
                                                      Set<String> typeParameterNameSet,
                                                      @NotNull AnnotationHolder annotationHolder,
                                                      @NotNull TextAttributesKey typeTextAttributesKey) {
        for (PsiElement child : mapUpdateArguments.getChildren()) {
            if (!(child instanceof Operator)) {
                highlightTypesAndTypeParameterUsages(
                        child,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                );
            }
        }
    }

    private void highlightTypesAndTypeParameterUsages(
            ElixirStabOperation stabOperation,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey typeTextAttributesKey) {
        ElixirStabParenthesesSignature stabParenthesesSignature = stabOperation.getStabParenthesesSignature();

        if (stabParenthesesSignature != null) {
            highlightTypesAndTypeParameterUsages(
                    stabParenthesesSignature,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else {
            ElixirStabNoParenthesesSignature stabNoParenthesesSignatures = stabOperation.getStabNoParenthesesSignature();

            if (stabNoParenthesesSignatures != null) {
                highlightTypesAndTypeParameterUsages(
                        stabNoParenthesesSignatures,
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                );
            }
        }

        ElixirStabBody stabBody = stabOperation.getStabBody();

        if (stabBody != null) {
            highlightTypesAndTypeParameterUsages(
                    stabBody,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        }
    }

    private void highlightTypesAndTypeParameterUsages(
            ElixirStabParenthesesSignature stabParenthesesSignature,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey typeTextAttributesKey) {
        PsiElement[] children = stabParenthesesSignature.getChildren();

        if (children.length == 1) {
            highlightTypesAndTypeParameterUsages(
                    children[0],
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (children.length != 3) {
            error("Cannot highlight types and type parameter usages", stabParenthesesSignature);
        }
    }

    private void highlightTypesAndTypeParameterUsages(
            ElixirStructOperation structOperation,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey typeTextAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                structOperation.getMapArguments(),
                typeParameterNameSet,
                annotationHolder,
                typeTextAttributesKey
        );
    }

    private void highlightTypesAndTypeParameterUsages(
            @NotNull Infix infix,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey typeTextAttributesKey) {
        PsiElement leftOperand = infix.leftOperand();

        if (leftOperand != null) {
            highlightTypesAndTypeParameterUsages(
                    leftOperand,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        }

        PsiElement rightOperand = infix.rightOperand();

        if (rightOperand != null) {
            highlightTypesAndTypeParameterUsages(
                    rightOperand,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        }
    }

    private void highlightTypesAndTypeParameterUsages(@NotNull PsiElement psiElement,
                                                      Set<String> typeParameterNameSet,
                                                      AnnotationHolder annotationHolder,
                                                      TextAttributesKey typeTextAttributesKey) {
        if (psiElement instanceof Arguments) {
            highlightTypesAndTypeParameterUsages(
                    (Arguments) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof ElixirAccessExpression ||
                psiElement instanceof ElixirAssociationsBase ||
                psiElement instanceof ElixirAssociations ||
                psiElement instanceof ElixirContainerAssociationOperation ||
                psiElement instanceof ElixirKeywordPair ||
                psiElement instanceof ElixirKeywords ||
                psiElement instanceof ElixirList ||
                psiElement instanceof ElixirMapArguments ||
                psiElement instanceof ElixirMapConstructionArguments ||
                psiElement instanceof ElixirNoParenthesesArguments ||
                psiElement instanceof ElixirParentheticalStab ||
                psiElement instanceof ElixirStab ||
                psiElement instanceof ElixirStabBody ||
                psiElement instanceof ElixirStabNoParenthesesSignature ||
                psiElement instanceof ElixirTuple) {
            highlightTypesAndTypeParameterUsages(
                    psiElement.getChildren(),
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof ElixirDecimalFloat) {
            highlightTypesAndTypeParameterUsages(
                    (ElixirDecimalFloat) psiElement,
                    annotationHolder
            );
        } else if (psiElement instanceof ElixirMapOperation) {
            highlightTypesAndTypeParameterUsages(
                    (ElixirMapOperation) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof ElixirMapUpdateArguments) {
            highlightTypesAndTypeParameterUsages(
                    (ElixirMapUpdateArguments) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof ElixirRelativeIdentifier ||
                psiElement instanceof UnqualifiedNoArgumentsCall) {
            // highlight entire element
            String name = psiElement.getText();
            TextAttributesKey textAttributesKey;

            if (typeParameterNameSet.contains(name)) {
                textAttributesKey = ElixirSyntaxHighlighter.TYPE_PARAMETER;
            } else {
                textAttributesKey = typeTextAttributesKey;
            }

            highlight(psiElement.getTextRange(), annotationHolder, textAttributesKey);
        } else if (psiElement instanceof ElixirStabParenthesesSignature) {
            highlightTypesAndTypeParameterUsages(
                    (ElixirStabParenthesesSignature) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof ElixirStabOperation) {
            highlightTypesAndTypeParameterUsages(
                    (ElixirStabOperation) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof ElixirStructOperation) {
            highlightTypesAndTypeParameterUsages(
                    (ElixirStructOperation) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof InterpolatedString) {
            highlightTypeError(psiElement, "Strings aren't allowed in types", annotationHolder);
        } else if (psiElement instanceof Infix && !(psiElement instanceof When)) {
            highlightTypesAndTypeParameterUsages(
                    (Infix) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof QualifiedParenthesesCall) {
            highlightTypesAndTypeParameterUsages(
                    (QualifiedParenthesesCall) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof QualifiedAlias) {
            highlightTypesAndTypeParameterUsages(
                    (QualifiedAlias) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof QualifiedNoArgumentsCall) {
            highlightTypesAndTypeParameterUsages(
                    (QualifiedNoArgumentsCall) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof QualifiedNoParenthesesCall) {
            highlightTypesAndTypeParameterUsages(
                    (QualifiedNoParenthesesCall) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof UnqualifiedNoParenthesesCall) {
            highlightTypesAndTypeParameterUsages(
                    (UnqualifiedNoParenthesesCall) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof UnqualifiedParenthesesCall) {
            highlightTypesAndTypeParameterUsages(
                    (UnqualifiedParenthesesCall) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (!(
                /* Occurs in the case of typing a {@code @type name ::} above a {@code @doc <HEREDOC>} and the
                   {@code @doc <HEREDOC>} is interpreted as the right-operand of {@code ::} */
                psiElement instanceof AtUnqualifiedNoParenthesesCall ||
                        // leave normal highlighting
                        psiElement instanceof BracketOperation ||
                        psiElement instanceof ElixirAlias ||
                        psiElement instanceof ElixirAtom ||
                        psiElement instanceof ElixirAtomKeyword ||
                        psiElement instanceof ElixirBitString ||
                        psiElement instanceof ElixirCharToken ||
                        psiElement instanceof ElixirDecimalWholeNumber ||
                        psiElement instanceof ElixirKeywordKey ||
                        /* happens when :: is typed in `@spec` above function clause that uses `do:` */
                        psiElement instanceof ElixirNoParenthesesKeywords ||
                        psiElement instanceof ElixirUnaryNumericOperation ||
                        psiElement instanceof ElixirMatchedUnaryOperation ||
                        psiElement instanceof ElixirVariable ||
                        psiElement instanceof ElixirUnmatchedUnaryOperation ||
                        psiElement instanceof When
        )) {
            cannotHighlightTypes(psiElement);
        }
    }

    private void highlightTypesAndTypeParameterUsages(
            PsiElement[] psiElements,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey textAttributesKey) {
        for (PsiElement psiElement : psiElements) {
            if (psiElement != null) {
                highlightTypesAndTypeParameterUsages(
                        psiElement,
                        typeParameterNameSet,
                        annotationHolder,
                        textAttributesKey
                );
            }
        }
    }

    private void highlightTypesAndTypeParameterUsages(QualifiedAlias qualifiedAlias,
                                                      Set<String> typeParameterNameSet,
                                                      AnnotationHolder annotationHolder,
                                                      TextAttributesKey textAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                qualifiedAlias.getFirstChild(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        );
        highlightTypesAndTypeParameterUsages(
                qualifiedAlias.getLastChild(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        );
    }

    private void highlightTypesAndTypeParameterUsages(
            QualifiedNoArgumentsCall qualifiedNoArgumentsCall,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey textAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                qualifiedNoArgumentsCall.getFirstChild(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        );
        highlightTypesAndTypeParameterUsages(
                qualifiedNoArgumentsCall.getRelativeIdentifier(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        );
    }

    private void highlightTypesAndTypeParameterUsages(
            QualifiedNoParenthesesCall qualifiedNoParenthesesCall,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey textAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                qualifiedNoParenthesesCall.getFirstChild(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        );
        highlightTypesAndTypeParameterUsages(
                qualifiedNoParenthesesCall.getRelativeIdentifier(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        );
        highlightTypesAndTypeParameterUsages(
                qualifiedNoParenthesesCall.primaryArguments(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        );
    }

    private void highlightTypesAndTypeParameterUsages(
            QualifiedParenthesesCall qualifiedParenthesesCall,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey textAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                qualifiedParenthesesCall.getFirstChild(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        );
        highlightTypesAndTypeParameterUsages(
                qualifiedParenthesesCall.getRelativeIdentifier(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        );
        highlightTypesAndTypeParameterUsages(
                qualifiedParenthesesCall.primaryArguments(),
                typeParameterNameSet,
                annotationHolder,
                textAttributesKey
        );

        PsiElement[] secondaryArguments = qualifiedParenthesesCall.secondaryArguments();

        if (secondaryArguments != null) {
            highlightTypesAndTypeParameterUsages(
                    secondaryArguments,
                    typeParameterNameSet,
                    annotationHolder,
                    textAttributesKey
            );
        }
    }

    private void highlightTypesAndTypeParameterUsages(
            UnqualifiedNoParenthesesCall unqualifiedNoParenthesesCall,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey typeTextAttributesKey) {
        PsiElement functionNameElement = unqualifiedNoParenthesesCall.functionNameElement();

        if (functionNameElement != null) {
            highlight(functionNameElement.getTextRange(), annotationHolder, typeTextAttributesKey);

            highlightTypesAndTypeParameterUsages(
                    unqualifiedNoParenthesesCall.primaryArguments(),
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else {
            error("Cannot highlight types and type parameter usages", unqualifiedNoParenthesesCall);
        }
    }

    private void highlightTypesAndTypeParameterUsages(
            UnqualifiedParenthesesCall unqualifiedParenthesesCall,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey typeTextAttributesKey) {
        if (!unqualifiedParenthesesCall.isCalling(KERNEL, UNQUOTE, 1)) {
            PsiElement functionNameElement = unqualifiedParenthesesCall.functionNameElement();

            if (functionNameElement != null) {


                highlight(functionNameElement.getTextRange(), annotationHolder, typeTextAttributesKey);

                highlightTypesAndTypeParameterUsages(
                        unqualifiedParenthesesCall.primaryArguments(),
                        typeParameterNameSet,
                        annotationHolder,
                        typeTextAttributesKey
                );

                PsiElement[] secondaryArguments = unqualifiedParenthesesCall.secondaryArguments();

                if (secondaryArguments != null) {
                    highlightTypesAndTypeParameterUsages(
                            secondaryArguments,
                            typeParameterNameSet,
                            annotationHolder,
                            typeTextAttributesKey
                    );
                }
            } else {
                error("Cannot highlight types and type parameter usages", unqualifiedParenthesesCall);
            }
        }
    }

    @NotNull
    private Set<String> specificationTypeParameterNameSet(ElixirKeywordPair keywordPair) {
        return Collections.singleton(keywordPair.getKeywordKey().getText());
    }

    @NotNull
    private Set<String> specificationTypeParameterNameSet(ElixirNoParenthesesKeywordPair noParenthesesKeywordPair) {
        return Collections.singleton(noParenthesesKeywordPair.getKeywordKey().getText());
    }

    /**
     * A type operator is an error, keyword pairs should be used for `when type: definition` for expression-local types,
     * but using `::` is a common error, so support it.
     */
    @NotNull
    private Set<String> specificationTypeParameterNameSet(Type type) {
        PsiElement leftOperand = type.leftOperand();
        Set<String> typeParameterNameSet;

        if (leftOperand != null) {
            typeParameterNameSet = Collections.singleton(leftOperand.getText());
        } else {
            error("Type does not have a left operand", type);
            typeParameterNameSet = Collections.emptySet();
        }

        return typeParameterNameSet;
    }

    private Set<String> specificationTypeParameterNameSet(@NotNull PsiElement psiElement) {
        Set<String> parameterNameSet;

        if (psiElement instanceof ElixirAccessExpression ||
                psiElement instanceof ElixirKeywords ||
                psiElement instanceof ElixirList ||
                psiElement instanceof ElixirNoParenthesesKeywords) {
            parameterNameSet = specificationTypeParameterNameSet(psiElement.getChildren());
        } else if (psiElement instanceof ElixirKeywordPair) {
            parameterNameSet = specificationTypeParameterNameSet((ElixirKeywordPair) psiElement);
        } else if (psiElement instanceof Type) {
            parameterNameSet = specificationTypeParameterNameSet((Type) psiElement);
        } else if (psiElement instanceof ElixirNoParenthesesKeywordPair) {
            parameterNameSet = specificationTypeParameterNameSet((ElixirNoParenthesesKeywordPair) psiElement);
        } else if (psiElement instanceof UnqualifiedNoArgumentsCall) {
            parameterNameSet = specificationTypeParameterNameSet((UnqualifiedNoArgumentsCall) psiElement);
        } else if (psiElement instanceof UnqualifiedNoParenthesesCall) {
            parameterNameSet = specificationTypeParameterNameSet((UnqualifiedNoParenthesesCall) psiElement);
        } else {
            error("Cannot extract specification type parameter name set", psiElement);
            parameterNameSet = Collections.emptySet();
        }

        return parameterNameSet;
    }

    private Set<String> specificationTypeParameterNameSet(PsiElement[] psiElements) {
        Set<String> accumulatedTypeParameterNameSet = new HashSet<>();

        for (PsiElement psiElement : psiElements) {
            accumulatedTypeParameterNameSet.addAll(specificationTypeParameterNameSet(psiElement));
        }

        return accumulatedTypeParameterNameSet;
    }

    /**
     * Occurs temporarily while typing before {@code :} in KeywordPairs after the {@code when}, such as in
     * {@code @spec foo(id) :: id when id} before finishing typing {@code @spec foo(id) :: id when id: String.t}.
     */
    private Set<String> specificationTypeParameterNameSet(UnqualifiedNoArgumentsCall unqualifiedNoArgumentsCall) {
        String name = unqualifiedNoArgumentsCall.functionName();
        Set<String> nameSet;

        if (name != null) {
            nameSet = Collections.singleton(unqualifiedNoArgumentsCall.functionName());
        } else {
            nameSet = Collections.emptySet();
        }

        return nameSet;
    }

    @NotNull
    private Set<String> specificationTypeParameterNameSet(
            @NotNull UnqualifiedNoParenthesesCall unqualifiedNoParenthesesCall
    ) {
        if (!CallDefinitionClause.is(unqualifiedNoParenthesesCall)) {
            error(
                    "Cannot extract specification type parameter name set from " +
                            "unqualifiedNoParenthesesCall that is a not a call definition clause",
                    unqualifiedNoParenthesesCall
            );
        }

        return Collections.emptySet();
    }

    /**
     * Assume bare aliases are incorrectly capitalized type parameters, say from someone's that used to generics
     * in Java.
     *
     * See https://github.com/KronicDeth/intellij-elixir/issues/694
     */
    @NotNull
    private Set<String> typeTypeParameterNameSet(@NotNull ElixirAlias alias) {
        return Collections.singleton(alias.getName());
    }

    private Set<String> typeTypeParameterNameSet(@NotNull ElixirTuple tuple) {
        Set<String> typeParameterNameSet = null;

        PsiElement[] children = tuple.getChildren();

        if (children.length == 3) {
            PsiElement firstChild = children[0];

            if (firstChild instanceof UnqualifiedNoArgumentsCall) {
                PsiElement strippedSecondChild = stripAccessExpression(children[1]);

                if (strippedSecondChild instanceof ElixirList) {
                    PsiElement strippedThirdChild = stripAccessExpression(children[2]);

                    if (strippedThirdChild instanceof ElixirAtomKeyword &&
                            strippedThirdChild.getText().equals("nil")) {
                        typeParameterNameSet = Collections.singleton(firstChild.getText());
                    }
                }
            }
        }

        if (typeParameterNameSet == null) {
            error("Cannot extract type type parameter name set", tuple);
            typeParameterNameSet = Collections.emptySet();
        }

        return typeParameterNameSet;
    }

    private Set<String> typeTypeParameterNameSet(PsiElement psiElement) {
        Set<String> typeParameterNameSet;

        if (psiElement instanceof ElixirAccessExpression) {
            typeParameterNameSet = typeTypeParameterNameSet(psiElement.getChildren());
        } else if (psiElement instanceof ElixirAlias) {
            /* Assume bare aliases are incorrectly capitalized type parameters, say from someone's that used to generics
               in Java.

               See https://github.com/KronicDeth/intellij-elixir/issues/694 */
            typeParameterNameSet = typeTypeParameterNameSet((ElixirAlias) psiElement);
        } else if (psiElement instanceof ElixirTuple) {
            typeParameterNameSet = typeTypeParameterNameSet((ElixirTuple) psiElement);
        } else if (psiElement instanceof ElixirUnmatchedUnqualifiedNoArgumentsCall) {
            typeParameterNameSet = Collections.singleton(psiElement.getText());
        } else {
            error("Cannot extract type type parameter name set", psiElement);
            typeParameterNameSet = Collections.emptySet();
        }

        return typeParameterNameSet;
    }

    private Set<String> typeTypeParameterNameSet(PsiElement[] psiElements) {
        Set<String> typeParameterNameSet = new HashSet<>();

        for (PsiElement psiElement : psiElements) {
            typeParameterNameSet.addAll(typeTypeParameterNameSet(psiElement));
        }

        return  typeParameterNameSet;
    }
}
