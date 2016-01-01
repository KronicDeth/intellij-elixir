package org.elixir_lang.annonator;

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
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.ElixirSyntaxHighlighter;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                    public void visitDeclaration(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
                        ASTNode node = atUnqualifiedNoParenthesesCall.getNode();
                        ASTNode[] identifierNodes = node.getChildren(ElixirPsiImplUtil.IDENTIFIER_TOKEN_SET);

                        assert identifierNodes.length == 1;

                        Quotable atPrefixOperator = atUnqualifiedNoParenthesesCall.getAtPrefixOperator();
                        ASTNode identifierNode = identifierNodes[0];

                        TextRange textRange = new TextRange(
                                atPrefixOperator.getTextRange().getStartOffset(),
                                identifierNode.getTextRange().getEndOffset()
                        );

                        String identifier = identifierNode.getText();

                        if (identifier.equals("callback") || identifier.equals("macrocallback")) {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);

                            highlightCallback(atUnqualifiedNoParenthesesCall, holder);
                        } else if (identifier.equals("doc") ||
                                identifier.equals("moduledoc") ||
                                identifier.equals("typedoc")) {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.DOCUMENTATION_MODULE_ATTRIBUTE);

                            highlightDocumentationText(atUnqualifiedNoParenthesesCall, holder);
                        } else if (identifier.equals("opaque") ||
                                identifier.equals("type") ||
                                identifier.equals("typep")) {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);

                            highlightType(atUnqualifiedNoParenthesesCall, holder);
                        } else if (identifier.equals("spec")) {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);

                            highlightSpecification(atUnqualifiedNoParenthesesCall, holder);
                        } else {
                            highlight(textRange, holder, ElixirSyntaxHighlighter.MODULE_ATTRIBUTE);
                        }
                    }

                    @Override
                    public void visitElement(@NotNull final PsiElement element) {
                        if (element instanceof AtNonNumericOperation) {
                            visitUsage((AtNonNumericOperation) element);
                        } else if (element instanceof AtUnqualifiedNoParenthesesCall) {
                            visitDeclaration((AtUnqualifiedNoParenthesesCall) element);
                        }
                    }

                    public void visitUsage(@NotNull final AtNonNumericOperation atNonNumericOperation) {
                        highlight(
                                atNonNumericOperation.getTextRange(),
                                holder,
                                ElixirSyntaxHighlighter.MODULE_ATTRIBUTE
                        );

                        if (atNonNumericOperation.getReference().resolve() == null) {
                            holder.createErrorAnnotation(atNonNumericOperation, "Unresolved module attribute");
                        }
                    }
                }
        );
    }

    /*
     * Private Instance Methods
     */

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
     *
     * @param atUnqualifiedNoParenthesesCall
     * @param annotationHolder
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
     * Highlights the function name of the declared @type, @typep, or @opaque as an {@link ElixirSyntaxHighlighter.TYPE}
     * and the its parameters as {@link ElixirSyntaxHighlighter.TYPE_PARAMETER}.
     */
    private void highlightType(@NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall,
                               @NotNull final AnnotationHolder annotationHolder) {
        PsiElement noParenthesesOneArgument = atUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument();
        PsiElement[] grandChildren = noParenthesesOneArgument.getChildren();

        if (grandChildren.length == 1) {
            PsiElement grandChild = grandChildren[0];

            if (grandChild instanceof ElixirMatchedMatchOperation) {
                // TODO LocalInspectionTool with quick fix to "Use `::`, not `=`, to separate types declarations from their definitions"
            } else if (grandChild instanceof ElixirMatchedTypeOperation) {
                InfixOperation infixOperation = (InfixOperation) grandChild;
                PsiElement leftOperand = infixOperation.leftOperand();
                Set<String> typeParameterNameSet = Collections.EMPTY_SET;

                if (leftOperand instanceof Call) {
                    Call call = (Call) leftOperand;
                    ASTNode functionNameNode = call.functionNameNode();

                    if (functionNameNode != null) {
                        highlight(
                                functionNameNode.getTextRange(),
                                annotationHolder,
                                ElixirSyntaxHighlighter.TYPE
                        );
                    }

                    if (call instanceof ElixirMatchedUnqualifiedNoArgumentsCall) {
                        // no arguments, so nothing else to do
                    } else if (call instanceof ElixirMatchedUnqualifiedParenthesesCall) {
                        PsiElement[] primaryArguments = call.primaryArguments();
                        PsiElement[] secondaryArguments = call.secondaryArguments();

                        /* if there are secondaryArguments, then it is the type parameters as in
                           `@type quote(type_name)(param1, param2) :: {param1, param2}` */
                        if (secondaryArguments != null) {
                            typeParameterNameSet = typeTypeParameterNameSet(secondaryArguments);

                            highlightTypesAndTypeParameterUsages(
                                    primaryArguments,
                                    /* as stated above, if there are secondary arguments, then the primary arguments are
                                       to quote or some equivalent metaprogramming. */
                                    Collections.EMPTY_SET,
                                    annotationHolder,
                                    ElixirSyntaxHighlighter.TYPE
                            );

                            highlightTypesAndTypeTypeParameterDeclarations(
                                    secondaryArguments,
                                    typeParameterNameSet,
                                    annotationHolder,
                                    ElixirSyntaxHighlighter.TYPE
                            );
                        } else if (primaryArguments != null) {
                            typeParameterNameSet = typeTypeParameterNameSet(primaryArguments);

                            highlightTypesAndTypeTypeParameterDeclarations(
                                    primaryArguments,
                                    typeParameterNameSet,
                                    annotationHolder,
                                    ElixirSyntaxHighlighter.TYPE
                            );
                        }
                    } else {
                        throw new NotImplementedException(
                                "Highlighting types for " + call.getClass().getCanonicalName() + " PsiElements is " +
                                        "not implemented yet.  Please open an issue " +
                                        "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the class " +
                                        "name of the sample text:\n" + ((PsiElement) call).getText()
                        );
                    }
                } else {
                    throw new NotImplementedException(
                            "Highlighting types for " + leftOperand.getClass().getCanonicalName() + " PsiElements is " +
                                    "not implemented yet.  Please open an issue " +
                                    "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the class name of " +
                                    "the sample text:\n" + leftOperand.getText()
                    );
                }

                PsiElement rightOperand = infixOperation.rightOperand();

                highlightTypesAndTypeParameterUsages(
                        rightOperand,
                        typeParameterNameSet,
                        annotationHolder,
                        ElixirSyntaxHighlighter.TYPE
                );
            } else if (grandChild instanceof ElixirMatchedUnqualifiedParenthesesCall) {
                // seen as `unquote(ast)`, but could also be just the beginning of typing
                ElixirMatchedUnqualifiedParenthesesCall matchedUnqualifiedParenthesesCall = (ElixirMatchedUnqualifiedParenthesesCall) grandChild;

                if (matchedUnqualifiedParenthesesCall.functionName().equals("unquote")) {
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
                    throw new NotImplementedException(
                            "Highlighting types for " +
                                    matchedUnqualifiedParenthesesCall.getClass().getCanonicalName() + " PsiElements " +
                                    "is not implemented yet.  Please open an issue " +
                                    "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the class name " +
                                    "of the sample text:\n" + grandChild.getText()
                    );
                }
            } else {
                throw new NotImplementedException(
                        "Highlighting types for " + grandChild.getClass().getCanonicalName() + " PsiElements is not " +
                                "implemented yet.  Please open an issue " +
                                "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the class name of " +
                                "the sample text:\n" + grandChild.getText()
                );
            }
        }
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
        if (psiElement instanceof ElixirUnmatchedUnqualifiedNoArgumentsCall) {
            highlightTypesAndTypeTypeParameterDeclarations(
                    (ElixirUnmatchedUnqualifiedNoArgumentsCall) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else {
            throw new NotImplementedException(
                    "Highlighting types and type type parameter declarations for " +
                            psiElement.getClass().getCanonicalName() + " PsiElements is not implemented yet.  Please " +
                            "open an issue (https://github.com/KronicDeth/intellij-elixir/issues/new) with the class " +
                            "name of the sample text:\n" + psiElement.getText()
            );
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
     * @param atUnqualifiedNoParenthesesCall
     * @param annotationHolder
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

            if (grandChild instanceof ElixirMatchedTypeOperation) {
                InfixOperation infixOperation = (InfixOperation) grandChild;
                PsiElement leftOperand = infixOperation.leftOperand();

                if (leftOperand instanceof Call) {
                    Call call = (Call) leftOperand;
                    ASTNode functionNameNode = call.functionNameNode();

                    if (functionNameNode != null) {
                        highlight(
                                functionNameNode.getTextRange(),
                                annotationHolder,
                                leftMostFunctionNameTextAttributesKey
                        );
                    }

                    PsiElement[] primaryArguments = call.primaryArguments();

                    if (primaryArguments != null) {
                        highlightTypesAndTypeParameterUsages(
                                primaryArguments,
                                Collections.EMPTY_SET,
                                annotationHolder,
                                leftMostFunctionArgumentsTextAttributesKey
                        );
                    }

                    PsiElement[] secondaryArguments = call.secondaryArguments();

                    if (secondaryArguments != null) {
                        highlightTypesAndTypeParameterUsages(
                                secondaryArguments,
                                Collections.EMPTY_SET,
                                annotationHolder,
                                leftMostFunctionArgumentsTextAttributesKey
                        );
                    }
                }

                PsiElement rightOperand = infixOperation.rightOperand();

                highlightTypesAndTypeParameterUsages(
                        rightOperand,
                        Collections.EMPTY_SET,
                        annotationHolder,
                        ElixirSyntaxHighlighter.TYPE
                );
            } else if (grandChild instanceof ElixirMatchedWhenOperation) {
                ElixirMatchedWhenOperation matchedWhenOperation = (ElixirMatchedWhenOperation) grandChild;
                Set<String> typeParameterNameSet = specificationTypeParameterNameSet(matchedWhenOperation.rightOperand());

                PsiElement leftOperand = matchedWhenOperation.leftOperand();

                if (leftOperand instanceof ElixirMatchedTypeOperation) {
                    ElixirMatchedTypeOperation matchedTypeOperation = (ElixirMatchedTypeOperation) leftOperand;
                    PsiElement matchedTypeOperationLeftOperand = matchedTypeOperation.leftOperand();

                    if (matchedTypeOperationLeftOperand instanceof Call) {
                        Call call = (Call) matchedTypeOperationLeftOperand;
                        ASTNode functionNameNode = call.functionNameNode();

                        if (functionNameNode != null) {
                            highlight(
                                    functionNameNode.getTextRange(),
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
                    } else {
                        throw new NotImplementedException(
                                "Highlighting types for " +
                                        matchedTypeOperationLeftOperand.getClass().getCanonicalName() +
                                        " PsiElements that are the left operand of " +
                                        matchedTypeOperation.getClass().getCanonicalName() + " is not implemented " +
                                        "yet.  Please open an issue " +
                                        "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the class " +
                                        "name of the sample text:\n" + matchedWhenOperation.getText()
                        );
                    }

                    PsiElement matchedTypeOperationRightOperand = matchedTypeOperation.rightOperand();

                    highlightTypesAndTypeParameterUsages(
                            matchedTypeOperationRightOperand,
                            typeParameterNameSet,
                            annotationHolder,
                            ElixirSyntaxHighlighter.TYPE
                    );
                } else {
                    throw new NotImplementedException(
                            "Highlighting types for " + leftOperand.getClass().getCanonicalName() + " PsiElements " +
                                    "that are the left operand of " +
                                    matchedWhenOperation.getClass().getCanonicalName() + " is not implemented yet.  " +
                                    "Please open an issue (https://github.com/KronicDeth/intellij-elixir/issues/new) " +
                                    "with the class name of the sample text:\n" + matchedWhenOperation.getText()
                    );
                }

                highlightTypesAndSpecificationTypeParameterDeclarations(
                        matchedWhenOperation.rightOperand(),
                        typeParameterNameSet,
                        annotationHolder,
                        ElixirSyntaxHighlighter.TYPE
                );
            }
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

    private void highlightTypesAndSpecificationTypeParameterDeclarations(PsiElement psiElement,
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
        } else {
            throw new NotImplementedException(
                    "Highlighting types and type parameter declarations for " +
                            psiElement.getClass().getCanonicalName() + " PsiElements is not implemented yet.  Please " +
                            "open an issue " + "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the " +
                            "class name of the sample text:\n" + psiElement.getText()
            );
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
        } else if (children.length == 3) {
            highlightTypesAndTypeParameterUsages(
                    (WhenOperation) stabParenthesesSignature,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else {
            throw new NotImplementedException(
                    "Highlighting types for " + stabParenthesesSignature.getClass().getCanonicalName() +
                            " PsiElements with neither 1 nor 3 children is not implemented yet.  Please open an " +
                            "issue (https://github.com/KronicDeth/intellij-elixir/issues/new) with the class name " +
                            "of the sample text:\n" + stabParenthesesSignature.getText()
            );
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
            InfixOperation infixOperation,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey typeTextAttributesKey) {
        highlightTypesAndTypeParameterUsages(
                infixOperation.leftOperand(),
                typeParameterNameSet,
                annotationHolder,
                typeTextAttributesKey
        );
        highlightTypesAndTypeParameterUsages(
                infixOperation.rightOperand(),
                typeParameterNameSet,
                annotationHolder,
                typeTextAttributesKey
        );
    }

    private void highlightTypesAndTypeParameterUsages(PsiElement psiElement,
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
                psiElement instanceof ElixirNoParenthesesExpression ||
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
        } else if (psiElement instanceof ElixirAlias ||
                psiElement instanceof ElixirAtom ||
                psiElement instanceof ElixirAtomKeyword ||
                psiElement instanceof ElixirBitString ||
                psiElement instanceof ElixirDecimalWholeNumber ||
                psiElement instanceof ElixirKeywordKey ||
                psiElement instanceof ElixirStringLine ||
                psiElement instanceof ElixirUnaryNumericOperation) {
            // leave normal highlighting
        }  else if (psiElement instanceof ElixirMapOperation) {
            highlightTypesAndTypeParameterUsages(
                    (ElixirMapOperation) psiElement,
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
        } else if (psiElement instanceof WhenOperation) {
            /* NOTE: MUST be before `InfixOperation` as `WhenOperation` is a subinterface of
              `InfixOperation` */
            highlightTypesAndTypeParameterUsages(
                    (WhenOperation) psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    typeTextAttributesKey
            );
        } else if (psiElement instanceof InfixOperation) {
            highlightTypesAndTypeParameterUsages(
                    (InfixOperation) psiElement,
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
        } else {
            throw new NotImplementedException(
                    "Highlighting types for " + psiElement.getClass().getCanonicalName() +
                            " PsiElements is not implemented yet.  Please open an issue " +
                            "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the class name of the " +
                            "sample text:\n" + psiElement.getText()
            );
        }
    }

    private void highlightTypesAndTypeParameterUsages(
            PsiElement[] psiElements,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey textAttributesKey) {
        for (PsiElement psiElement : psiElements) {
            highlightTypesAndTypeParameterUsages(
                    psiElement,
                    typeParameterNameSet,
                    annotationHolder,
                    textAttributesKey
            );
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
        ASTNode functionNameNode = unqualifiedNoParenthesesCall.functionNameNode();
        assert functionNameNode != null;
        highlight(functionNameNode.getTextRange(), annotationHolder, typeTextAttributesKey);

        highlightTypesAndTypeParameterUsages(
                unqualifiedNoParenthesesCall.primaryArguments(),
                typeParameterNameSet,
                annotationHolder,
                typeTextAttributesKey
        );
    }

    private void highlightTypesAndTypeParameterUsages(
            UnqualifiedParenthesesCall unqualifiedParenthesesCall,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey typeTextAttributesKey) {
        ASTNode functionNameNode = unqualifiedParenthesesCall.functionNameNode();
        assert functionNameNode != null;
        highlight(functionNameNode.getTextRange(), annotationHolder, typeTextAttributesKey);

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
    }

    private void highlightTypesAndTypeParameterUsages(
            WhenOperation whenOperation,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey typeTextAttributesKey) {
        return;
    }

    private void highlightTypesAndSpecificationTypeParameterDeclarations(
            ElixirUnmatchedUnqualifiedNoArgumentsCall unmatchedUnqualifiedNoArgumentsCall,
            Set<String> typeParameterNameSet,
            AnnotationHolder annotationHolder,
            TextAttributesKey textAttributesKey
    ) {
        String variable = unmatchedUnqualifiedNoArgumentsCall.getText();

        if (typeParameterNameSet.contains(variable)) {
            highlight(
                    unmatchedUnqualifiedNoArgumentsCall.getTextRange(),
                    annotationHolder,
                    ElixirSyntaxHighlighter.TYPE_PARAMETER
            );
        } else {
            highlightTypesAndTypeParameterUsages(
                    unmatchedUnqualifiedNoArgumentsCall,
                    typeParameterNameSet,
                    annotationHolder,
                    textAttributesKey
            );
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

    private Set<String> specificationTypeParameterNameSet(PsiElement psiElement) {
        if (psiElement instanceof ElixirAccessExpression ||
                psiElement instanceof ElixirKeywords ||
                psiElement instanceof ElixirList ||
                psiElement instanceof ElixirNoParenthesesKeywords) {
            return specificationTypeParameterNameSet(psiElement.getChildren());
        } else if (psiElement instanceof ElixirKeywordPair) {
            return specificationTypeParameterNameSet((ElixirKeywordPair) psiElement);
        } else if (psiElement instanceof ElixirNoParenthesesKeywordPair) {
            return specificationTypeParameterNameSet((ElixirNoParenthesesKeywordPair) psiElement);
        } else {
            throw new NotImplementedException(
                    "Extracting specification type parameter name set for " + psiElement.getClass().getCanonicalName() +
                            " PsiElements is not implemented yet.  Please open an issue " +
                            "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the class name of the " +
                            "sample text:\n" + psiElement.getText()
            );
        }
    }

    private Set<String> specificationTypeParameterNameSet(PsiElement[] psiElements) {
        Set<String> accumulatedTypeParameterNameSet = new HashSet<String>();

        for (PsiElement psiElement : psiElements) {
            accumulatedTypeParameterNameSet.addAll(specificationTypeParameterNameSet(psiElement));
        }

        return accumulatedTypeParameterNameSet;
    }

    private Set<String> typeTypeParameterNameSet(
            ElixirMatchedUnqualifiedParenthesesCall matchedUnqualifiedParenthesesCall
    ) {
        Set<String> typeParameterNameSet = new HashSet<String>();

        typeParameterNameSet.addAll(typeTypeParameterNameSet(matchedUnqualifiedParenthesesCall.primaryArguments()));
        typeParameterNameSet.addAll(typeTypeParameterNameSet(matchedUnqualifiedParenthesesCall.secondaryArguments()));

        return typeParameterNameSet;
    }

    private Set<String> typeTypeParameterNameSet(PsiElement psiElement) {
        if (psiElement instanceof ElixirUnmatchedUnqualifiedNoArgumentsCall) {
            return Collections.singleton(psiElement.getText());
        } else {
            throw new NotImplementedException(
                    "Extracting type type parameter name set for " + psiElement.getClass().getCanonicalName() +
                            " PsiElements is not implemented yet.  Please open an issue " +
                            "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the class name of the " +
                            "sample text:\n" + psiElement.getText()
            );
        }
    }

    private Set<String> typeTypeParameterNameSet(PsiElement[] psiElements) {
        Set<String> typeParameerNameSet = new HashSet<String>();

        for (PsiElement psiElement : psiElements) {
            typeParameerNameSet.addAll(typeTypeParameterNameSet(psiElement));
        }

        return  typeParameerNameSet;
    }
}
