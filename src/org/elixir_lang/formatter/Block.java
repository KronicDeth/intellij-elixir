package org.elixir_lang.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.containers.Predicate;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.code_style.CodeStyleSettings;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.elixir_lang.psi.ElixirTypes.*;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.*;

/**
 * @note MUST implement {@link BlockEx} or language-specific indent settings will NOT be used and only the generic ones
 * will be used.
 */
public class Block extends AbstractBlock implements BlockEx {
    private static final TokenSet ARROW_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_ARROW_OPERATION,
            ElixirTypes.UNMATCHED_ARROW_OPERATION
    );
    private static final TokenSet BODY_TOKEN_SET = TokenSet.create(
            ElixirTypes.INTERPOLATED_CHAR_LIST_BODY,
            ElixirTypes.INTERPOLATED_REGEX_BODY,
            ElixirTypes.INTERPOLATED_SIGIL_BODY,
            ElixirTypes.INTERPOLATED_STRING_BODY,
            ElixirTypes.INTERPOLATED_WORDS_BODY,
            ElixirTypes.LITERAL_CHAR_LIST_BODY,
            ElixirTypes.LITERAL_REGEX_BODY,
            ElixirTypes.LITERAL_SIGIL_BODY,
            ElixirTypes.LITERAL_STRING_BODY,
            ElixirTypes.LITERAL_WORDS_BODY,
            ElixirTypes.QUOTE_CHAR_LIST_BODY,
            ElixirTypes.QUOTE_STRING_BODY
    );
    private static final TokenSet BOOLEAN_WORD_OPERATOR_TOKEN_SET = TokenSet.create(
            ElixirTypes.AND_WORD_OPERATOR,
            ElixirTypes.OR_WORD_OPERATOR
    );
    private static final TokenSet CAPTURE_NON_NUMERIC_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_CAPTURE_NON_NUMERIC_OPERATION,
            ElixirTypes.UNMATCHED_CAPTURE_NON_NUMERIC_OPERATION
    );
    private static final TokenSet COMPARISON_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_COMPARISON_OPERATION,
            ElixirTypes.UNMATCHED_COMPARISON_OPERATION
    );
    private static final TokenSet HEREDOC_LINE_TOKEN_SET = TokenSet.create(
            ElixirTypes.CHAR_LIST_HEREDOC_LINE,
            ElixirTypes.INTERPOLATED_CHAR_LIST_HEREDOC_LINE,
            ElixirTypes.INTERPOLATED_REGEX_HEREDOC_LINE,
            ElixirTypes.INTERPOLATED_SIGIL_HEREDOC_LINE,
            ElixirTypes.INTERPOLATED_STRING_HEREDOC_LINE,
            ElixirTypes.INTERPOLATED_WORDS_HEREDOC_LINE,
            ElixirTypes.LITERAL_CHAR_LIST_HEREDOC_LINE,
            ElixirTypes.LITERAL_REGEX_HEREDOC_LINE,
            ElixirTypes.LITERAL_SIGIL_HEREDOC_LINE,
            ElixirTypes.LITERAL_STRING_HEREDOC_LINE,
            ElixirTypes.LITERAL_WORDS_HEREDOC_LINE,
            ElixirTypes.STRING_HEREDOC_LINE
    );
    private static final TokenSet HEREDOC_TOKEN_SET = TokenSet.create(
            CHAR_LIST_HEREDOC,
            INTERPOLATED_CHAR_LIST_SIGIL_HEREDOC,
            INTERPOLATED_REGEX_HEREDOC,
            INTERPOLATED_SIGIL_HEREDOC,
            INTERPOLATED_STRING_SIGIL_HEREDOC,
            INTERPOLATED_WORDS_HEREDOC,
            LITERAL_CHAR_LIST_SIGIL_HEREDOC,
            LITERAL_REGEX_HEREDOC,
            LITERAL_SIGIL_HEREDOC,
            LITERAL_STRING_SIGIL_HEREDOC,
            LITERAL_WORDS_HEREDOC,
            STRING_HEREDOC
    );
    private static final TokenSet LINE_TOKEN_SET = TokenSet.create(
            ElixirTypes.CHAR_LIST_LINE,
            ElixirTypes.INTERPOLATED_CHAR_LIST_SIGIL_LINE,
            ElixirTypes.INTERPOLATED_REGEX_LINE,
            ElixirTypes.INTERPOLATED_SIGIL_LINE,
            ElixirTypes.INTERPOLATED_STRING_SIGIL_LINE,
            ElixirTypes.INTERPOLATED_WORDS_LINE,
            ElixirTypes.LITERAL_CHAR_LIST_SIGIL_LINE,
            ElixirTypes.LITERAL_REGEX_LINE,
            ElixirTypes.LITERAL_SIGIL_LINE,
            ElixirTypes.LITERAL_STRING_SIGIL_LINE,
            ElixirTypes.LITERAL_WORDS_LINE,
            ElixirTypes.STRING_LINE
    );
    private static final TokenSet MAP_OPERATOR_TOKEN_SET = TokenSet.create(ElixirTypes.MAP_OPERATOR);
    private static final TokenSet MAP_TOKEN_SET = TokenSet.create(
            ElixirTypes.MAP_OPERATION,
            ElixirTypes.STRUCT_OPERATION
    );
    private static final TokenSet MATCHED_CALL_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_AT_UNQUALIFIED_NO_PARENTHESES_CALL,
            ElixirTypes.MATCHED_DOT_CALL,
            ElixirTypes.MATCHED_QUALIFIED_NO_ARGUMENTS_CALL,
            ElixirTypes.MATCHED_QUALIFIED_NO_PARENTHESES_CALL,
            ElixirTypes.MATCHED_QUALIFIED_PARENTHESES_CALL,
            ElixirTypes.MATCHED_UNQUALIFIED_NO_ARGUMENTS_CALL,
            ElixirTypes.MATCHED_UNQUALIFIED_NO_PARENTHESES_CALL,
            ElixirTypes.MATCHED_UNQUALIFIED_PARENTHESES_CALL
    );
    private static final TokenSet MATCH_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_MATCH_OPERATION,
            ElixirTypes.UNMATCHED_MATCH_OPERATION
    );
    private static final TokenSet ENFORCE_INDENT_TO_CHILDREN_TOKEN_SET = TokenSet.orSet(
            COMPARISON_OPERATION_TOKEN_SET,
            MATCH_OPERATION_TOKEN_SET
    );
    private static final TokenSet MULTIPLICATION_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_MULTIPLICATION_OPERATION,
            ElixirTypes.UNMATCHED_MULTIPLICATION_OPERATION
    );
    private static final TokenSet NO_ARGUMENTS_CALL_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_QUALIFIED_NO_ARGUMENTS_CALL,
            ElixirTypes.MATCHED_UNQUALIFIED_NO_ARGUMENTS_CALL,
            ElixirTypes.UNMATCHED_QUALIFIED_NO_ARGUMENTS_CALL,
            ElixirTypes.UNMATCHED_UNQUALIFIED_NO_ARGUMENTS_CALL
    );
    private static final TokenSet OPERATION_TOKEN_SET = TokenSet.orSet(
            TokenSet.create(
                    ElixirTypes.MATCHED_ADDITION_OPERATION,
                    ElixirTypes.MATCHED_CAPTURE_NON_NUMERIC_OPERATION,
                    ElixirTypes.MATCHED_IN_MATCH_OPERATION,
                    ElixirTypes.MATCHED_IN_OPERATION,
                    ElixirTypes.MATCHED_MATCH_OPERATION,
                    ElixirTypes.MATCHED_MULTIPLICATION_OPERATION,
                    ElixirTypes.MATCHED_RELATIONAL_OPERATION,
                    ElixirTypes.MATCHED_THREE_OPERATION,
                    ElixirTypes.MATCHED_UNARY_NON_NUMERIC_OPERATION,
                    ElixirTypes.MATCHED_WHEN_OPERATION,
                    ElixirTypes.UNARY_NUMERIC_OPERATION,
                    ElixirTypes.UNMATCHED_ADDITION_OPERATION,
                    ElixirTypes.UNMATCHED_CAPTURE_NON_NUMERIC_OPERATION,
                    ElixirTypes.UNMATCHED_IN_MATCH_OPERATION,
                    ElixirTypes.UNMATCHED_IN_OPERATION,
                    ElixirTypes.UNMATCHED_MATCH_OPERATION,
                    ElixirTypes.UNMATCHED_MULTIPLICATION_OPERATION,
                    ElixirTypes.UNMATCHED_RELATIONAL_OPERATION,
                    ElixirTypes.UNMATCHED_THREE_OPERATION,
                    ElixirTypes.UNMATCHED_UNARY_NON_NUMERIC_OPERATION,
                    ElixirTypes.UNMATCHED_WHEN_OPERATION
            ),
            COMPARISON_OPERATION_TOKEN_SET,
            MATCH_OPERATION_TOKEN_SET
    );
    private static final TokenSet OPERATOR_RULE_TOKEN_SET = TokenSet.create(
            ElixirTypes.ADDITION_INFIX_OPERATOR,
            ElixirTypes.ARROW_INFIX_OPERATOR,
            ElixirTypes.AT_PREFIX_OPERATOR,
            ElixirTypes.CAPTURE_PREFIX_OPERATOR,
            ElixirTypes.COMPARISON_INFIX_OPERATOR,
            ElixirTypes.DOT_INFIX_OPERATOR,
            ElixirTypes.IN_INFIX_OPERATOR,
            ElixirTypes.IN_MATCH_INFIX_OPERATOR,
            ElixirTypes.MATCH_INFIX_OPERATOR,
            ElixirTypes.MULTIPLICATION_INFIX_OPERATOR,
            ElixirTypes.RELATIONAL_INFIX_OPERATOR,
            ElixirTypes.STAB_INFIX_OPERATOR,
            ElixirTypes.THREE_INFIX_OPERATOR,
            ElixirTypes.UNARY_PREFIX_OPERATOR,
            ElixirTypes.WHEN_INFIX_OPERATOR
    );
    private static final TokenSet OPERATOR_TOKEN_SET = TokenSet.orSet(
            ADDITION_OPERATOR_TOKEN_SET,
            AND_OPERATOR_TOKEN_SET,
            ARROW_OPERATOR_TOKEN_SET,
            AT_OPERATOR_TOKEN_SET,
            CAPTURE_OPERATOR_TOKEN_SET,
            COMPARISON_OPERATOR_TOKEN_SET,
            DOT_OPERATOR_TOKEN_SET,
            IN_OPERATOR_TOKEN_SET,
            IN_MATCH_OPERATOR_TOKEN_SET,
            MAP_OPERATOR_TOKEN_SET,
            MATCH_OPERATOR_TOKEN_SET,
            MULTIPLICATIVE_OPERATOR_TOKEN_SET,
            PIPE_OPERATOR_TOKEN_SET,
            OR_OPERATOR_TOKEN_SET,
            RELATIONAL_OPERATOR_TOKEN_SET,
            STAB_OPERATOR_TOKEN_SET,
            STRUCT_OPERATOR_TOKEN_SET,
            THREE_OPERATOR_TOKEN_SET,
            TYPE_OPERATOR_TOKEN_SET,
            TWO_OPERATOR_TOKEN_SET,
            UNARY_OPERATOR_TOKEN_SET,
            WHEN_OPERATOR_TOKEN_SET
    );
    private static final TokenSet PIPE_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_PIPE_OPERATION,
            ElixirTypes.UNMATCHED_PIPE_OPERATION
    );
    // "Sometimes" because only the `and` and `or` operators will be affected
    private static final TokenSet SOMETIMES_BOOLEAN_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_AND_OPERATION,
            ElixirTypes.MATCHED_OR_OPERATION,
            ElixirTypes.UNMATCHED_AND_OPERATION,
            ElixirTypes.UNMATCHED_OR_OPERATION
    );
    // "Sometimes" because only the `and` and `or` operators will be affected
    private static final TokenSet SOMETIMES_BOOLEAN_OPERATOR_RULE_TOKEN_SET = TokenSet.create(
            ElixirTypes.AND_INFIX_OPERATOR,
            ElixirTypes.OR_INFIX_OPERATOR
    );
    private static final TokenSet TUPLISH_TOKEN_SET = TokenSet.create(ElixirTypes.MULTIPLE_ALIASES, ElixirTypes.TUPLE);
    private static final TokenSet TWO_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_TWO_OPERATION,
            ElixirTypes.UNMATCHED_TWO_OPERATION
    );
    private static final TokenSet TYPE_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_TYPE_OPERATION,
            ElixirTypes.UNMATCHED_TYPE_OPERATION
    );
    private static final TokenSet UNINDENTED_ONLY_ARGUMENT_TOKEN_SET = TokenSet.orSet(
            TokenSet.create(ElixirTypes.ANONYMOUS_FUNCTION, ElixirTypes.LIST, ElixirTypes.TUPLE),
            HEREDOC_TOKEN_SET,
            MAP_TOKEN_SET
    );
    private static final TokenSet UNMATCHED_CALL_TOKEN_SET = TokenSet.create(
            ElixirTypes.UNMATCHED_AT_UNQUALIFIED_NO_PARENTHESES_CALL,
            ElixirTypes.UNMATCHED_DOT_CALL,
            ElixirTypes.UNMATCHED_QUALIFIED_NO_ARGUMENTS_CALL,
            ElixirTypes.UNMATCHED_QUALIFIED_NO_PARENTHESES_CALL,
            ElixirTypes.UNMATCHED_QUALIFIED_PARENTHESES_CALL,
            ElixirTypes.UNMATCHED_UNQUALIFIED_NO_ARGUMENTS_CALL,
            ElixirTypes.UNMATCHED_UNQUALIFIED_NO_PARENTHESES_CALL,
            ElixirTypes.UNMATCHED_UNQUALIFIED_PARENTHESES_CALL
    );
    private static final TokenSet WHEN_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_WHEN_OPERATION,
            ElixirTypes.UNMATCHED_WHEN_OPERATION
    );
    private static final TokenSet WHITESPACE_TOKEN_SET =
            TokenSet.create(ElixirTypes.EOL, TokenType.WHITE_SPACE, ElixirTypes.SIGNIFICANT_WHITE_SPACE);
    @Nullable
    private final Wrap childrenWrap;
    @Nullable
    private final Indent indent;
    @NotNull
    private final SpacingBuilder spacingBuilder;

    public Block(@NotNull ASTNode node, @NotNull SpacingBuilder spacingBuilder) {
        this(node, null, null, spacingBuilder, null, null);
    }

    public Block(@NotNull ASTNode node,
                 @Nullable Wrap wrap,
                 @Nullable Alignment alignment,
                 @NotNull SpacingBuilder spacingBuilder,
                 @Nullable Indent indent,
                 @Nullable Wrap childrenWrap) {
        super(node, wrap, alignment);
        this.spacingBuilder = spacingBuilder;
        this.indent = indent;
        this.childrenWrap = childrenWrap;

        IElementType elementType = node.getElementType();

        assert elementType != ElixirTypes.ACCESS_EXPRESSION : "accessExpressions should be flattened with " +
                "buildAccessExpressionChildren";

        if (elementType == ElixirTypes.STAB_OPERATION) {
            assert childrenWrap != null : "stabOperation must have a non-null childrenWrap, so that sibling " +
                    "stabOperations and their children are wrapped consistently";
        }
    }

    @NotNull
    static List<com.intellij.formatting.Block> buildChildren(
            @NotNull ASTNode node,
            @NotNull BlockListReducer blockListReducer) {
        List<com.intellij.formatting.Block> blockList = new ArrayList<>();

        ASTNode child = node.getFirstChildNode();

        while (child != null) {
            if (shouldBuildBlock(child)) {
                blockList = blockListReducer.reduce(child, child.getElementType(), blockList);
            }

            child = child.getTreeNext();
        }

        return blockList;
    }

    private static boolean hasAtLeastCountChildren(@NotNull CompositeElement parent,
                                                   @NotNull IElementType childElementType,
                                                   int atLeastCount) {
        int count = 0;
        for (ASTNode child = parent.getFirstChildNode();
             child != null && count < atLeastCount;
             child = child.getTreeNext()) {
            if (child.getElementType() == childElementType) {
                count++;
            }
        }

        return count >= atLeastCount;
    }

    private static boolean shouldBuildBlock(@NotNull IElementType childElementType) {
        return !WHITESPACE_TOKEN_SET.contains(childElementType);
    }

    private static boolean shouldBuildBlock(@NotNull ASTNode child) {
        return shouldBuildBlock(child.getElementType());
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAccessExpressionChildren(@NotNull ASTNode accessExpression) {
        return buildAccessExpressionChildren(accessExpression, null, null, null);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAccessExpressionChildren(@NotNull ASTNode accessExpression,
                                                                              @Nullable Alignment childrenAlignment) {
        return buildAccessExpressionChildren(accessExpression, null, childrenAlignment, null);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAccessExpressionChildren(@NotNull ASTNode accessExpression,
                                                                              @Nullable Wrap childrenWrap) {
        return buildAccessExpressionChildren(accessExpression, childrenWrap, null, null);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAccessExpressionChildren(@NotNull ASTNode accessExpression,
                                                                              @Nullable Wrap childrenWrap,
                                                                              @Nullable Alignment childrenAlignment) {
        return buildAccessExpressionChildren(accessExpression, childrenWrap, childrenAlignment, null);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAccessExpressionChildren(@NotNull ASTNode accessExpression,
                                                                              @Nullable Wrap childrenWrap,
                                                                              @Nullable Indent childrenIndent) {
        return buildAccessExpressionChildren(accessExpression, childrenWrap, null, childrenIndent);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAccessExpressionChildren(
            @NotNull ASTNode accessExpression,
            @Nullable Wrap childrenWrap,
            @Nullable Alignment childrenAlignment,
            @Nullable Indent childrenIndent
    ) {
        return buildChildren(
                accessExpression,
                (child, childElementType, blockList) -> {
                    blockList.add(buildChild(child, childrenWrap, childrenAlignment, childrenIndent));

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAlignedOperandsOperationChildren(
            @NotNull ASTNode operation,
            @NotNull Predicate<CodeStyleSettings> alignOperands,
            @NotNull IElementType operatorRuleElementType) {
        return buildAlignedOperandsOperationChildren(
                operation,
                alignOperands,
                TokenSet.create(operatorRuleElementType)
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAlignedOperandsOperationChildren(
            @NotNull ASTNode operation,
            @NotNull Predicate<CodeStyleSettings> alignOperands,
            @NotNull TokenSet operatorRuleTokenSet) {
        Alignment operandAlignment;

        if (alignOperands.apply(codeStyleSettings(operation))) {
            operandAlignment = Alignment.createAlignment();
        } else {
            operandAlignment = null;
        }

        return buildChildren(
                operation,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blockList.addAll(buildAccessExpressionChildren(child, operandAlignment));
                    } else if (operatorRuleTokenSet.contains(childElementType)) {
                        blockList.addAll(buildOperatorRuleChildren(child, null, null, operandAlignment));
                    } else {
                        blockList.add(buildChild(child, operandAlignment));
                    }

                    return blockList;
                }
        );
    }

    /**
     * Builds anonymousFunction FN, stab, and END as siblings.  If it is a one-liner, the END is setup to wrap once
     * all other part wraps, such as due to chopping.
     */
    @NotNull
    private List<com.intellij.formatting.Block> buildAnonymousFunctionChildren(@NotNull ASTNode anonymousFunction) {
        final Alignment childrenAlignment = Alignment.createAlignment();
        Wrap endWrap;
        Wrap stabBodyChildrenWrap;

        if (anonymousFunction.textContains('\n')) {
            endWrap = Wrap.createWrap(WrapType.ALWAYS, true);
            stabBodyChildrenWrap = null;
        } else {
            // if `end` wraps, then the function should be de-one-liner-ed, so all wraps are shared after the ->
            endWrap = Wrap.createWrap(WrapType.CHOP_DOWN_IF_LONG, true);
            stabBodyChildrenWrap = endWrap;
        }

        return buildChildren(
                anonymousFunction,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.END) {
                        blockList.add(buildChild(child, endWrap, Indent.getNoneIndent()));
                    } else if (childElementType == END_OF_EXPRESSION) {
                        blockList.addAll(
                                buildEndOfExpressionChildren(child, childrenAlignment, Indent.getNormalIndent())
                        );
                    } else if (childElementType == ElixirTypes.STAB) {
                        blockList.addAll(buildStabChildren((CompositeElement) child, stabBodyChildrenWrap));
                    } else {
                        blockList.add(buildChild(child));
                    }

                    return blockList;
                }
        );
    }

    /**
     * @param startAlignment the alignment for the start of the arrow operation (pipeline) chain.  If {@code null} is
     *                       given, then the left operand of the {@code arrowOperation} is given an alignment and it
     *                       will be the startAlignment or any arrowOperations in the right operand.
     */
    @NotNull
    private List<com.intellij.formatting.Block> buildArrowOperationChildren(@NotNull ASTNode arrowOperation,
                                                                            @Nullable Alignment startAlignment) {
        Alignment operatorAlignment;
        final Alignment[] operandAlignment = new Alignment[1];
        Alignment nestedStartAlignment;

        if (startAlignment == null) {
            nestedStartAlignment = Alignment.createAlignment();
        } else {
            nestedStartAlignment = startAlignment;
        }

        // if no arrow operations remain, then first operand in buildChildren lambda will be start
        if (arrowOperation.findChildByType(ARROW_OPERATION_TOKEN_SET) == null) {
            operandAlignment[0] = nestedStartAlignment;
        } else {
            operandAlignment[0] = null;
        }

        operatorAlignment = nestedStartAlignment;

        return buildChildren(
                arrowOperation,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blockList.addAll(
                                buildAccessExpressionChildren(child, operandAlignment[0])
                        );
                    } else if (ARROW_OPERATION_TOKEN_SET.contains(childElementType)) {
                        blockList.addAll(buildArrowOperationChildren(child, nestedStartAlignment));
                    } else if (childElementType == ElixirTypes.ARROW_INFIX_OPERATOR) {
                        blockList.addAll(buildOperatorRuleChildren(child, operatorAlignment));
                        /* right operand has alignment only so that any children can align to it instead of operator,
                           which ensures that unmatched call do block's end aligns with start of the call instead of
                           the arrow operator */
                        operandAlignment[0] = Alignment.createAlignment();
                    } else {
                        blockList.add(buildChild(child, operandAlignment[0]));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAssociationsBaseChildren(
            @NotNull ASTNode associationsBase,
            @NotNull Wrap containerAssociationOperationWrap
    ) {
        return buildChildren(
                associationsBase,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.CONTAINER_ASSOCIATION_OPERATION) {
                        blockList.add(buildChild(child, containerAssociationOperationWrap, Indent.getNormalIndent()));
                    } else {
                        blockList.add(buildChild(child));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAssociationsChildren(@NotNull ASTNode associations,
                                                                          @NotNull Wrap associationExpressionWrap) {
        return buildChildren(
                associations,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.ASSOCIATIONS_BASE) {
                        blockList.addAll(buildAssociationsBaseChildren(child, associationExpressionWrap));
                    } else {
                        // comma or comment
                        blockList.add(buildChild(child));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildBlockIdentifierChildren(
            @NotNull ASTNode blockIdentifier,
            @Nullable Alignment blockIdentifierAlignment
    ) {
        Wrap childrenWrap = Wrap.createWrap(WrapType.ALWAYS, true);

        return buildChildren(
                blockIdentifier,
                (child, childElementType, blockList) -> {
                    blockList.add(buildChild(child, childrenWrap, blockIdentifierAlignment));

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildBlockItemChildren(@NotNull ASTNode blockItem,
                                                                       @Nullable Alignment blockItemAlignment) {
        return buildChildren(
                blockItem,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.BLOCK_IDENTIFIER) {
                        blockList.addAll(buildBlockIdentifierChildren(child, blockItemAlignment));
                    } else if (childElementType == ElixirTypes.END_OF_EXPRESSION) {
                        blockList.addAll(buildEndOfExpressionChildren(child, null, null));
                    } else if (childElementType == ElixirTypes.STAB) {
                        Wrap stabBodyChildrenWrap;

                        if (child.findChildByType(ElixirTypes.STAB_BODY) != null) {
                            stabBodyChildrenWrap = Wrap.createWrap(WrapType.ALWAYS, true);
                        } else {
                            stabBodyChildrenWrap = null;
                        }

                        blockList.addAll(buildStabChildren((CompositeElement) child, stabBodyChildrenWrap));
                    } else {
                        blockList.add(buildChild(child));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildBlockListChildren(@NotNull ASTNode blockListNode,
                                                                       @Nullable Alignment parentAlignment) {
        return buildChildren(
                blockListNode,
                (child, childElementType, blockList) -> {
                    blockList.add(buildChild(child, parentAlignment, Indent.getNoneIndent()));

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildBodyChildren(@NotNull ASTNode body, @Nullable Wrap childrenWrap) {
        return buildChildren(
                body,
                (child, childElementType, blockList) -> {
                    blockList.add(buildChild(child, childrenWrap));

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildCallChildChildren(@NotNull ASTNode child,
                                                                       @NotNull IElementType childElementType,
                                                                       @Nullable Wrap callWrap,
                                                                       @Nullable Alignment callAlignment) {
        List<com.intellij.formatting.Block> blockList = new ArrayList<>();

        if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
            blockList.addAll(buildAccessExpressionChildren(child));
        } else if (childElementType == ElixirTypes.IDENTIFIER) {
            blockList.addAll(buildIdentifierChildren(child));
        } else if (childElementType == ElixirTypes.MATCHED_PARENTHESES_ARGUMENTS) {
            blockList.addAll(buildMatchedParenthesesArguments(child, callWrap, callAlignment));
        } else if (childElementType == ElixirTypes.NO_PARENTHESES_ONE_ARGUMENT) {
            blockList.addAll(buildNoParenthesesOneArgument(child, callWrap, callAlignment));
        } else if (OPERATOR_RULE_TOKEN_SET.contains(childElementType)) {
            blockList.addAll(buildOperatorRuleChildren(child));
        } else {
            blockList.add(buildChild(child));
        }

        return blockList;
    }

    /**
     * {@link #getSpacing(com.intellij.formatting.Block, com.intellij.formatting.Block)} only has the parent block and
     * the two direct children as context for evaluating rules, so to distinguish normal division `/` from `/` in
     * `&NAME/ARITY`, for `&...` need to look ahead and down and if `NAME/ARITY` is detected then the `/` operation will
     * need to flattened so that the `DIVISION_OPERATOR` itself is an immediate child of
     * `MATCHED_CAPTURE_NON_NUMERIC_OPERATION` or `UNMATCHED_CAPTURE_NON_NUMERIC_OPERATION`.
     *
     * @param captureNonNumericOperation A capture operation that may be a name/arity reference.
     */
    @NotNull
    private List<com.intellij.formatting.Block> buildCaptureNonNumericOperationChildren(
            ASTNode captureNonNumericOperation
    ) {
        return buildChildren(
                captureNonNumericOperation,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blockList.addAll(buildAccessExpressionChildren(child));
                    } else if (childElementType == ElixirTypes.CAPTURE_PREFIX_OPERATOR) {
                        blockList.addAll(buildOperatorRuleChildren(child));
                    } else if (MULTIPLICATION_OPERATION_TOKEN_SET.contains(childElementType)) {
                        blockList.addAll(
                                buildCapturedMultiplicationOperationChildren(child)
                        );
                    } else {
                        blockList.add(buildChild(child));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildCapturedMultiplicationOperationChildren(
            ASTNode multiplicationOperation
    ) {
        List<com.intellij.formatting.Block> flattenedBlockList = buildOperationChildren(multiplicationOperation);
        List<com.intellij.formatting.Block> blockList = null;

        if (flattenedBlockList.size() == 3) {
            ASTBlock operatorBlock = (ASTBlock) flattenedBlockList.get(1);

            if (operatorBlock.getNode().getElementType() == ElixirTypes.DIVISION_OPERATOR) {
                ASTBlock rightOperandBlock = (ASTBlock) flattenedBlockList.get(2);

                // `/ARITY` confirmed
                if (rightOperandBlock.getNode().getElementType() == ElixirTypes.DECIMAL_WHOLE_NUMBER) {
                    ASTBlock leftOperandBlock = (ASTBlock) flattenedBlockList.get(0);
                    ASTNode leftOperand = leftOperandBlock.getNode();
                    IElementType leftOperandElementType = leftOperand.getElementType();

                    if (NO_ARGUMENTS_CALL_TOKEN_SET.contains(leftOperandElementType)) {
                        // `NAME/ARITY` confirmed
                        if (leftOperand.findChildByType(ElixirTypes.DO_BLOCK) == null) {
                            blockList = flattenedBlockList;
                        }
                    }
                }
            }
        }

        if (blockList == null) {
            // don't flatten
            blockList = Collections.singletonList(buildChild(multiplicationOperation));
        }

        return blockList;
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child) {
        return new Block(child, spacingBuilder);
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child, @Nullable Alignment alignment) {
        return new Block(child, null, alignment, spacingBuilder, null, null);
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child, @NotNull Indent indent) {
        return new Block(child, null, null, spacingBuilder, indent, null);
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child, @Nullable Wrap wrap) {
        return new Block(child, wrap, null, spacingBuilder, null, null);
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child, @Nullable Wrap wrap, @Nullable Alignment alignment) {
        return new Block(child, wrap, alignment, spacingBuilder, null, null);
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child, @Nullable Wrap wrap, @Nullable Indent indent) {
        return new Block(child, wrap, null, spacingBuilder, indent, null);
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child, @Nullable Alignment alignment, @Nullable Indent indent) {
        return new Block(child, null, alignment, spacingBuilder, indent, null);
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child,
                             @Nullable Wrap wrap,
                             @Nullable Alignment alignment,
                             @Nullable Indent indent) {
        return new Block(child, wrap, alignment, spacingBuilder, indent, null);
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child,
                             @Nullable Wrap wrap,
                             @Nullable Indent indent,
                             @Nullable Wrap childrenWrap) {
        return new Block(
                child,
                wrap,
                null,
                spacingBuilder,
                indent,
                childrenWrap
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildChildren(@NotNull ASTNode parent,
                                                              @Nullable Wrap parentWrap,
                                                              @Nullable Alignment parentAlignment,
                                                              @Nullable Wrap childrenWrap) {
        List<com.intellij.formatting.Block> blocks;
        IElementType parentElementType = parent.getElementType();

        if (parentElementType == ElixirTypes.ANONYMOUS_FUNCTION) {
            blocks = buildAnonymousFunctionChildren(parent);
        } else if (ARROW_OPERATION_TOKEN_SET.contains(parentElementType)) {
            blocks = buildArrowOperationChildren(parent, null);
        } else if (parentElementType == ElixirTypes.BLOCK_ITEM) {
            blocks = buildBlockItemChildren(parent, parentAlignment);
        } else if (CAPTURE_NON_NUMERIC_OPERATION_TOKEN_SET.contains(parentElementType)) {
            blocks = buildCaptureNonNumericOperationChildren(parent);
        } else if (parentElementType == ElixirTypes.CONTAINER_ASSOCIATION_OPERATION) {
            blocks = buildContainerAssociationOperation(parent);
        } else if (HEREDOC_TOKEN_SET.contains(parentElementType)) {
            blocks = buildHeredocChildren((CompositeElement) parent);
        } else if (parentElementType == ElixirTypes.INTERPOLATION) {
            blocks = buildInterpolationChildren(parent);
        } else if (parentElementType == ElixirTypes.KEYWORD_PAIR) {
            blocks = buildKeywordPairChildren(parent);
        } else if (parentElementType == ElixirTypes.KEYWORD_KEY) {
            blocks = buildKeywordKeyChildren(parent);
        } else if (LINE_TOKEN_SET.contains(parentElementType)) {
            blocks = buildLineChildren(parent);
        } else if (parentElementType == ElixirTypes.LIST) {
            blocks = buildListChildren(parent);
        } else if (MATCHED_CALL_TOKEN_SET.contains(parentElementType)) {
            blocks = buildMatchedCallChildren(parent, parentWrap, parentAlignment);
        } else if (MAP_TOKEN_SET.contains(parentElementType)) {
            blocks = buildMapChildren(parent, childrenWrap);
        } else if (parentElementType == ElixirTypes.MULTIPLE_ALIASES) {
            blocks = buildMultipleAliasesChildren(parent);
        } else if (parentElementType == ElixirTypes.PARENTHETICAL_STAB) {
            blocks = buildParentheticalStabChildren(parent);
        } else if (parentElementType == ElixirTypes.STAB_OPERATION) {
            //noinspection ConstantConditions
            blocks = buildStabOperationChildren(parent, childrenWrap);
        } else if (parentElementType == ElixirTypes.TUPLE) {
            blocks = buildTupleChildren(parent);
        } else if (WHEN_OPERATION_TOKEN_SET.contains(parentElementType)) {
            blocks = buildWhenOperationChildren(parent);
        } else if (OPERATION_TOKEN_SET.contains(parentElementType)) {
            blocks = buildOperationChildren(parent);
        } else if (PIPE_OPERATION_TOKEN_SET.contains(parentElementType)) {
            blocks = buildPipeOperationChildren(parent);
        } else if (SOMETIMES_BOOLEAN_OPERATION_TOKEN_SET.contains(parentElementType)) {
            blocks = buildSometimesBooleanOperationChildren(parent);
        } else if (TWO_OPERATION_TOKEN_SET.contains(parentElementType)) {
            blocks = buildTwoOperationChildren(parent);
        } else if (TYPE_OPERATION_TOKEN_SET.contains(parentElementType)) {
            blocks = buildTypeOperationChildren(parent);
        } else if (UNMATCHED_CALL_TOKEN_SET.contains(parentElementType)) {
            blocks = buildUnmatchedCallChildren(parent, parentWrap, parentAlignment);
        } else {
            /* all children need a shared alignment, so that the second child doesn't have an automatic continuation
               indent */
            final Alignment childrenAlignment = Alignment.createChildAlignment(parentAlignment);

            blocks = buildChildren(
                    parent,
                    (child, childElementType, blockList) -> {
                        if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                            blockList.addAll(
                                    buildAccessExpressionChildren(child, childrenAlignment)
                            );
                        } else if (ENFORCE_INDENT_TO_CHILDREN_TOKEN_SET.contains(childElementType)) {
                            blockList.add(
                                    buildChild(
                                            child,
                                            childrenAlignment,
                                            Indent.getIndent(
                                                    Indent.Type.NONE,
                                                    true,
                                                    /* `enforceIndentToChildren = true`, so that `do` blocks none-indent
                                                       aligns to start of child when child is argument to `assert`  */
                                                    true
                                            )
                                    )
                            );
                        } else if (OPERATOR_RULE_TOKEN_SET.contains(childElementType)) {
                            blockList.addAll(buildOperatorRuleChildren(child));
                        } else if (childElementType == END_OF_EXPRESSION) {
                            blockList.addAll(
                                    // None Indent because comments should align to left
                                    buildEndOfExpressionChildren(child, childrenAlignment, Indent.getNoneIndent())
                            );
                        } else if (childElementType == ElixirTypes.WHEN_INFIX_OPERATOR) {
                            blockList.addAll(buildOperatorRuleChildren(child));
                        } else {
                            blockList.add(
                                    buildChild(child, childrenAlignment)
                            );
                        }

                        return blockList;
                    }
            );
        }

        return blocks;
    }

    @Override
    protected List<com.intellij.formatting.Block> buildChildren() {
        return buildChildren(myNode, myWrap, myAlignment, childrenWrap);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildContainerAssociationOperation(
            @NotNull ASTNode containerAssociationOperation
    ) {
        Wrap keyWrap = Wrap.createWrap(WrapType.NORMAL, true);
        Wrap associationOperatorWrap = Wrap.createChildWrap(keyWrap, WrapType.NONE, true);
        Wrap valueWrap = Wrap.createWrap(WrapType.NORMAL, true);
        final Wrap[] operandWrap = {keyWrap};
        final boolean[] rightOperand = {false};

        return buildChildren(
                containerAssociationOperation,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        if (rightOperand[0]) {
                            blockList.addAll(buildMapValueAccessExpressionChildren(child));
                        } else {
                            blockList.addAll(buildAccessExpressionChildren(child, operandWrap[0]));
                        }
                    } else if (childElementType == ElixirTypes.ASSOCIATION_OPERATOR) {
                        blockList.add(buildChild(child, associationOperatorWrap));
                        operandWrap[0] = valueWrap;
                        rightOperand[0] = true;
                    } else {
                        blockList.add(buildChild(child, operandWrap[0]));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildContainerChildren(@NotNull ASTNode container,
                                                                       @NotNull IElementType openingElementType,
                                                                       @Nullable Wrap openingWrap,
                                                                       @NotNull IElementType closingElementType) {
        return buildContainerChildren(
                container,
                openingElementType,
                openingWrap,
                closingElementType,
                null,
                (child, childElementType, tailWrap, childrenIndent, blockList) -> {
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blockList.addAll(buildAccessExpressionChildren(child, tailWrap, childrenIndent));
                    } else if (childElementType == ElixirTypes.COMMA) {
                        blockList.add(buildChild(child));
                    } else if (childElementType == ElixirTypes.END_OF_EXPRESSION) {
                        blockList.addAll(buildEndOfExpressionChildren(child, null, childrenIndent));
                    } else if (childElementType == ElixirTypes.KEYWORDS) {
                        blockList.addAll(buildKeywordsChildren(child, tailWrap));
                    } else {
                        blockList.add(buildChild(child, tailWrap, childrenIndent));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildContainerChildren(
            @NotNull ASTNode container,
            @NotNull IElementType openingElementType,
            @Nullable Wrap openingWrap,
            @NotNull IElementType closingElementType,
            @Nullable Wrap givenTailWrap,
            @NotNull ContainerBlockListReducer containerBlockListReducer
    ) {
        Indent childrenIndent = Indent.getNormalIndent();
        Wrap nonEmptyTailWrap;

        if (givenTailWrap != null) {
            nonEmptyTailWrap = givenTailWrap;
        } else {
            nonEmptyTailWrap = tailWrap(container, openingElementType, closingElementType);
        }

        // empty tailWrap
        final Wrap[] tailWrap = {Wrap.createWrap(WrapType.NORMAL, true)};

        return buildChildren(
                container,
                (child, childElementType, blockList) -> {
                    if (childElementType == closingElementType) {
                        blockList.add(buildChild(child, tailWrap[0], Indent.getNoneIndent()));
                    } else if (childElementType == openingElementType) {
                        blockList.add(buildChild(child, openingWrap));
                    } else {
                        tailWrap[0] = nonEmptyTailWrap;

                        blockList = containerBlockListReducer.reduce(
                                child,
                                childElementType,
                                tailWrap[0],
                                childrenIndent,
                                blockList
                        );
                    }

                    return blockList;
                }
        );
    }

    /**
     * Builds doBlock DO, stab.*, and END as siblings, so they can all be indented relative to the parent unmatched call
     * from {@link #buildUnmatchedCallChildren(ASTNode, Wrap, Alignment)}
     *
     * @param doBlock doBlock that is a child of an unmatched call, but needs to be flattened for formatting
     * @return the flattened children of the doBlock: DO, stab.*, and END.
     */
    @NotNull
    private List<com.intellij.formatting.Block> buildDoBlockChildren(@NotNull ASTNode doBlock,
                                                                     @Nullable Alignment parentAlignment) {
        boolean hasBLockLists = hasAtLeastCountChildren((CompositeElement) doBlock, ElixirTypes.BLOCK_LIST, 1);

        return buildChildren(
                doBlock,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.BLOCK_LIST) {
                        blockList.addAll(buildBlockListChildren(child, parentAlignment));
                    } else if (childElementType == ElixirTypes.END) {
                        Wrap endWrap;

                        if (hasBLockLists) {
                            endWrap = Wrap.createWrap(WrapType.ALWAYS, true);
                        } else {
                            endWrap = Wrap.createWrap(WrapType.NORMAL, true);
                        }

                        boolean indentRelativeToDirectParent =
                                codeStyleSettings(child).ALIGN_UNMATCHED_CALL_DO_BLOCKS ==
                                        CodeStyleSettings.UnmatchedCallDoBlockAlignment.CALL.value;

                        blockList.add(
                                buildChild(
                                        child,
                                        endWrap,
                                        parentAlignment,
                                        Indent.getIndent(Indent.Type.NONE, indentRelativeToDirectParent, false)
                                )
                        );
                    } else if (childElementType == END_OF_EXPRESSION) {
                        blockList.addAll(
                                buildEndOfExpressionChildren(child, null, Indent.getNormalIndent())
                        );
                    } else if (childElementType == ElixirTypes.STAB) {
                        Wrap stabBodyChildrenWrap;

                        if (child.findChildByType(ElixirTypes.STAB_BODY) != null) {
                            stabBodyChildrenWrap = Wrap.createWrap(WrapType.ALWAYS, true);
                        } else {
                            stabBodyChildrenWrap = null;
                        }

                        blockList.addAll(buildStabChildren((CompositeElement) child, stabBodyChildrenWrap));
                    } else {
                        blockList.add(buildChild(child));
                    }

                    return blockList;
                }
        );
    }

    /**
     * Builds endOfExpression.*.  Importantly, it separates out the EOLs, which are whitespace from the comments that
     * may be interlaced
     */
    @NotNull
    private List<com.intellij.formatting.Block> buildEndOfExpressionChildren(@NotNull ASTNode endOfExpression,
                                                                             @Nullable Alignment childAlignment,
                                                                             @Nullable Indent childIndent) {
        return buildChildren(
                endOfExpression,
                (child, childElementType, blockList) -> {
                    blockList.add(buildChild(child, childAlignment, childIndent));

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildHeredocChildren(CompositeElement heredoc) {
        ASTNode heredocPrefix = heredoc.findChildByType(ElixirTypes.HEREDOC_PREFIX);
        int heredocPrefixLength = 0;

        if (heredocPrefix != null) {
            heredocPrefixLength = heredocPrefix.getTextLength();
        }

        Indent indent = Indent.getNoneIndent();
        int finalHeredocPrefixLength = heredocPrefixLength;

        return buildChildren(
                heredoc,
                (child, childElementType, blockList) -> {
                    if (HEREDOC_LINE_TOKEN_SET.contains(childElementType)) {
                        blockList.addAll(buildHeredocLineChildren(child, finalHeredocPrefixLength));
                    } else if (childElementType != ElixirTypes.HEREDOC_PREFIX) {
                        /* The heredocPrefix while important for determining the significant white space in each heredoc
                           line, is normal, insignificant whitespace when shifting the lines, so it has no block */
                        blockList.add(buildChild(child, indent));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildHeredocLineChildren(ASTNode heredocLine, int heredocPrefixLength) {
        List<com.intellij.formatting.Block> blockList;

        if (heredocLine.getTextLength() == 1 && heredocLine.getText().equals("\n")) {
            // prevent insertion of prefix length spaces on blank lines
            blockList = Collections.emptyList();
        } else {
           blockList = Collections.singletonList(
                   new HeredocLineBlock(heredocLine, heredocPrefixLength, spacingBuilder)
           );
        }

        return blockList;
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildIdentifierChildren(ASTNode identifier) {
        return buildChildren(
                identifier,
                (child, childElementType, blockList) -> {
                    blockList.add(buildChild(child));

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildInterpolationChildren(@NotNull ASTNode interpolation) {
        return buildContainerChildren(
                interpolation,
                ElixirTypes.INTERPOLATION_START,
                Wrap.createWrap(WrapType.NONE, false),
                ElixirTypes.INTERPOLATION_END
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildKeywordKeyChildren(@NotNull ASTNode keywordKey) {
        return buildChildren(
                keywordKey,
                (child, childElementType, blockList) -> {
                    blockList.add(buildChild(child));

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildKeywordPairChildren(@NotNull ASTNode keywordPair) {
        Wrap keywordKeyWrap = Wrap.createWrap(WrapType.NORMAL, true);
        Wrap keywordPairColonWrap  = Wrap.createChildWrap(keywordKeyWrap, WrapType.NONE, true);
        Wrap keywordValueWrap = Wrap.createWrap(WrapType.NORMAL, true);

        return buildChildren(
                keywordPair,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blockList.addAll(buildMapValueAccessExpressionChildren(child));
                    } else if (childElementType == ElixirTypes.KEYWORD_KEY) {
                        blockList.add(buildChild(child, keywordKeyWrap));
                    } else if (childElementType == ElixirTypes.KEYWORD_PAIR_COLON) {
                        blockList.add(buildChild(child, keywordPairColonWrap));
                    } else {
                        blockList.add(buildChild(child, keywordValueWrap));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildKeywordsChildren(@NotNull ASTNode keywords,
                                                                      @NotNull Wrap keywordPairWrap) {

        return buildChildren(
                keywords,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.KEYWORD_PAIR) {
                        blockList.add(buildChild(child, keywordPairWrap, Indent.getNormalIndent()));
                    } else {
                        // commas and comments
                        blockList.add(buildChild(child));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildLineChildren(@NotNull ASTNode line) {
        // Everything on a line contains significant white space, so no automatic newlines should be inserted
        Wrap none = Wrap.createWrap(WrapType.NONE, true);

        return buildChildren(
                line,
                (child, childElementType, blockList) -> {
                    if (BODY_TOKEN_SET.contains(childElementType)) {
                        /* Flatten body because its children represent textual elements that can't be aligned without
                           changing their meaning, so there's no need for a formatting block for them */
                        blockList.addAll(buildBodyChildren(child, none));
                    } else {
                        blockList.add(buildChild(child, none));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildListChildren(@NotNull ASTNode list) {
        return buildContainerChildren(list, ElixirTypes.OPENING_BRACKET, null, ElixirTypes.CLOSING_BRACKET);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildMapArgumentsChildren(@NotNull ASTNode mapArguments,
                                                                          @Nullable Wrap mapArgumentsWrap) {
        return buildContainerChildren(
                mapArguments,
                ElixirTypes.OPENING_CURLY,
                null,
                ElixirTypes.CLOSING_CURLY,
                mapArgumentsWrap,
                (child, childElementType, tailWrap, childrenIndent, blockList) -> {
                    if (childElementType == ElixirTypes.MAP_CONSTRUCTION_ARGUMENTS) {
                        blockList.addAll(buildMapConstructArgumentsChildren(child, tailWrap));
                    } else if (childElementType == ElixirTypes.MAP_UPDATE_ARGUMENTS) {
                        blockList.addAll(buildMapUpdateArgumentsChildren(child, tailWrap));
                    } else {
                        blockList.add(buildChild(child, tailWrap, Indent.getNormalIndent()));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildMapChildren(@NotNull ASTNode map,
                                                                 @Nullable Wrap mapArgumentsWrap) {
        return buildChildren(
                map,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.MAP_ARGUMENTS) {
                        blockList.addAll(buildMapArgumentsChildren(child, mapArgumentsWrap));
                    } else if (childElementType == ElixirTypes.MAP_PREFIX_OPERATOR) {
                        blockList.addAll(buildOperatorRuleChildren(child));
                    } else {
                        blockList.add(buildChild(child));
                    }

                    return blockList;
                }
        );
    }

    /**
     *
     * @param mapArgumentsTailWrap {@link Wrap} shared between the mapConstructionArguments and
     *   {@link ElixirTypes#CLOSING_CURLY}.
     */
    @NotNull
    private List<com.intellij.formatting.Block> buildMapConstructArgumentsChildren(
            @NotNull ASTNode mapConstructionArguments,
            @NotNull Wrap mapArgumentsTailWrap
    ) {
        return buildChildren(
                mapConstructionArguments,
                (child, childElementType, blockList) -> {
                    blockList.addAll(buildMapTailArgumentsChildChildren(child, childElementType, mapArgumentsTailWrap));

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildMapTailArgumentsChildChildren(
            @NotNull ASTNode child,
            @NotNull IElementType childElementType,
            @NotNull Wrap mapArgumentsTailWrap
    ) {
        List<com.intellij.formatting.Block> blockList = new ArrayList<>();

        if (childElementType == ElixirTypes.ASSOCIATIONS_BASE) {
            blockList.addAll(buildAssociationsBaseChildren(child, mapArgumentsTailWrap));
        } else if (childElementType == ElixirTypes.ASSOCIATIONS) {
            blockList.addAll(buildAssociationsChildren(child, mapArgumentsTailWrap));
        } else if (childElementType == ElixirTypes.KEYWORDS) {
            blockList.addAll(buildKeywordsChildren(child, mapArgumentsTailWrap));
        } else {
            blockList.add(buildChild(child));
        }

        return blockList;
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildMapUpdateArgumentsChildren(
            @NotNull ASTNode mapUpdateArguments,
            @NotNull Wrap mapArgumentsTailWrap
    ) {
        return buildChildren(
                mapUpdateArguments,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.COMMA) {
                        blockList.add(buildChild(child));
                    } else if (childElementType == ElixirTypes.PIPE_INFIX_OPERATOR) {
                        blockList.addAll(buildOperatorRuleChildren(child));
                    } else {
                        blockList.addAll(
                                buildMapTailArgumentsChildChildren(child, childElementType, mapArgumentsTailWrap)
                        );
                    }

                    return blockList;
                }
        );
    }

    /* Nested maps and structs will be in accessExpressions, so can't use `buildAccessExpression` or the nested maps and
       structs won't be wrapped, so that nested keywordKeys don't appear on the same line */
    @NotNull
    private List<com.intellij.formatting.Block> buildMapValueAccessExpressionChildren(
            @NotNull ASTNode mapValueAccessExpression
    ) {
        return buildChildren(
                mapValueAccessExpression,
                (grandChild, grandChildElementType, childBlockList) -> {
                    if (MAP_TOKEN_SET.contains(grandChildElementType)) {
                        childBlockList.add(
                                buildChild(
                                        grandChild,
                                        null,
                                        null,
                                        Wrap.createWrap(WrapType.ALWAYS, true)
                                )
                        );
                    } else {
                        childBlockList.add(buildChild(grandChild));
                    }

                    return childBlockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildMatchedCallChildren(@NotNull ASTNode matchedCall,
                                                                         @Nullable Wrap parentWrap,
                                                                         @Nullable Alignment parentAlignment) {
        return buildChildren(
                matchedCall,
                (child, childElementType, blockList) -> {
                    blockList.addAll(
                            buildCallChildChildren(
                                    child,
                                    childElementType,
                                    parentWrap,
                                    parentAlignment
                            )
                    );

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildMatchedParenthesesArguments(
            @NotNull ASTNode matchedParenthesesArguments,
            @Nullable Wrap parentWrap,
            @Nullable Alignment parentAlignment) {
        return buildChildren(
                matchedParenthesesArguments,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.PARENTHESES_ARGUMENTS) {
                        blockList.addAll(buildParenthesesArgumentsChildren(child, parentWrap, parentAlignment));
                    } else {
                        blockList.add(buildChild(child));
                    }

                    return blockList;
                }
        );
    }

    /**
     * The whole point of adding multiple aliases to Elixir was to allow for compact `import`s and `aliases`, so unlike
     * actual tuples, where `WrapType.ALWAYS` makes sense for multiline tuples, for multi-line multiple aliases, we
     * want the most compact arrangement that still preserves alignment inside the `{` `}` and wrapping when the line is
     * too long.
     */
    @NotNull
    private List<com.intellij.formatting.Block> buildMultipleAliasesChildren(@NotNull ASTNode multipleAliases) {
        // Align all aliases so when they wrap they line up inside the `{` instead of with the `{`.
        Alignment aliasAlignment = Alignment.createAlignment();
        // The same Wrap can be used for multiple comma-separated values in the example code for `Wrap.createWrap` ...
        Wrap aliasWrap = Wrap.createWrap(
                WrapType.NORMAL,
                /* ... that code say that `wrapFirstElement` should be `false` if only those elements past margin should
                   be wrapped. */
                true
        );
        Wrap commaWrap = Wrap.createChildWrap(aliasWrap, WrapType.NONE, true);

        return buildChildren(
                multipleAliases,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.CLOSING_CURLY) {
                        blockList.add(buildChild(child));
                    } else if (childElementType == ElixirTypes.OPENING_CURLY) {
                        blockList.add(buildChild(child, Wrap.createWrap(WrapType.NONE, false)));
                    } else if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blockList.addAll(buildAccessExpressionChildren(child, aliasWrap, aliasAlignment));
                    } else if (childElementType == ElixirTypes.COMMA) {
                        blockList.add(buildChild(child, commaWrap));
                    } else {
                        blockList.add(buildChild(child, aliasWrap, aliasAlignment));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildNoParenthesesOneArgument(
            @NotNull ASTNode child,
            @Nullable Wrap parentWrap,
            @Nullable Alignment parentAlignment
    ) {
        List<com.intellij.formatting.Block> blockList = buildChildren(child, parentWrap, parentAlignment, null);

        if (blockList.size() == 1) {
            Block block = (Block) blockList.get(0);
            ASTNode blockNode = block.myNode;

            if (UNINDENTED_ONLY_ARGUMENT_TOKEN_SET.contains(blockNode.getElementType())) {
                blockList = Collections.singletonList(
                    /* Clear alignment, so that it allows anonymous functions and heredocs to align with call when they
                       are the only argument.  The Alignment.createChildAlignment does this when there is more than one
                       argument.  I don't know why it doesn't work with only 1 argument. */
                        new Block(
                                blockNode,
                                block.myWrap,
                                null,
                                block.spacingBuilder,
                                block.indent,
                                block.childrenWrap
                        )
                );
            }
        }

        return blockList;
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildOperationChildren(ASTNode operation) {
        return buildChildren(
                operation,
                (child, childElementType, blockList) -> {
                    /* Move the operator token ASTNode up, out of the operator rule ASTNode as the operator rule ASTNode
                       is only there to consume EOLs around the operator token ASTNode and EOLs will ignored */
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blockList.addAll(
                                buildAccessExpressionChildren(child)
                        );
                    } else if (OPERATOR_RULE_TOKEN_SET.contains(childElementType)) {
                        blockList.addAll(buildOperatorRuleChildren(child));
                    } else {
                        blockList.add(buildChild(child));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildOperatorRuleChildren(@NotNull ASTNode operatorRule) {
        return buildOperatorRuleChildren(operatorRule, null, null, null);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildOperatorRuleChildren(@NotNull ASTNode operatorRule,
                                                                          @Nullable Alignment operatorAlignment) {
        return buildOperatorRuleChildren(operatorRule, null, operatorAlignment, null);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildOperatorRuleChildren(@NotNull ASTNode operatorRule,
                                                                          @Nullable Wrap operatorWrap) {
        return buildOperatorRuleChildren(operatorRule, operatorWrap, null, null);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildOperatorRuleChildren(
            @NotNull ASTNode operatorRule,
            @Nullable Wrap operatorWrap,
            @Nullable Alignment operatorAlignment,
            @Nullable Alignment rightCommentAlignment
    ) {
        final Alignment[] commentAlignment = {operatorAlignment};
        Indent operatorIndent = Indent.getNoneIndent();
        final Indent[] commentIndent = {operatorIndent};


        return buildChildren(
                operatorRule,
                (child, childElementType, blockList) -> {
                    if (OPERATOR_TOKEN_SET.contains(childElementType)) {
                        blockList.add(buildChild(child, operatorWrap, operatorAlignment, operatorIndent));
                        commentAlignment[0] = rightCommentAlignment;
                        commentIndent[0] = null;
                    } else {
                        blockList.add(buildChild(child, commentAlignment[0], commentIndent[0]));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildParenthesesArgumentsChildren(
            @NotNull ASTNode parenthesesArguments,
            @Nullable Wrap parentWrap,
            @Nullable Alignment parentAlignment) {
        Wrap tailWrap = tailWrap(
                parenthesesArguments,
                ElixirTypes.OPENING_PARENTHESIS,
                ElixirTypes.CLOSING_PARENTHESIS
        );

        return buildChildren(
                parenthesesArguments,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        // arguments
                        blockList.addAll(buildAccessExpressionChildren(child, tailWrap, Indent.getNormalIndent()));
                    } else if (childElementType == ElixirTypes.CLOSING_PARENTHESIS) {
                        blockList.add(buildChild(child, tailWrap, parentAlignment, Indent.getNoneIndent()));
                    } else if (childElementType == ElixirTypes.COMMA) {
                        blockList.add(buildChild(child));
                    } else if (childElementType == ElixirTypes.KEYWORDS) {
                        blockList.addAll(buildKeywordsChildren(child, tailWrap));
                    } else if (childElementType == ElixirTypes.OPENING_PARENTHESIS) {
                        blockList.add(
                                buildChild(child, Wrap.createChildWrap(parentWrap, WrapType.CHOP_DOWN_IF_LONG, true))
                        );
                    } else {
                        // arguments that aren't keywords.
                        blockList.add(buildChild(child, tailWrap, Indent.getNormalIndent()));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildParentheticalStabChildren(@NotNull ASTNode parentheticalStab) {
        Indent childIndent = Indent.getNormalIndent();
        Wrap stabBodyChildrenWrap;

        if (parentheticalStab.textContains('\n')) {
            stabBodyChildrenWrap = null;
        } else {
            stabBodyChildrenWrap = Wrap.createWrap(WrapType.CHOP_DOWN_IF_LONG, true);
        }

        return buildChildren(
                parentheticalStab,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.SEMICOLON) {
                        blockList.add(
                                buildChild(child, Wrap.createWrap(WrapType.NONE, true), childIndent)
                        );
                    } else if (childElementType == ElixirTypes.STAB) {
                        blockList.addAll(buildStabChildren((CompositeElement) child, stabBodyChildrenWrap));
                    } else {
                        blockList.add(buildChild(child));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildPipeOperationChildren(@NotNull ASTNode pipeOperation) {
        //noinspection ConstantConditions
        return buildAlignedOperandsOperationChildren(
                pipeOperation,
                (codeStyleSettings) -> codeStyleSettings.ALIGN_PIPE_OPERANDS,
                ElixirTypes.PIPE_INFIX_OPERATOR
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildSometimesBooleanOperationChildren(
            @NotNull ASTNode sometimesBooleanOperation
    ) {
        return buildAlignedOperandsOperationChildren(
                sometimesBooleanOperation,
                (codeStyleSettings) -> {
                    ASTNode operatorRule = sometimesBooleanOperation.findChildByType(
                            SOMETIMES_BOOLEAN_OPERATOR_RULE_TOKEN_SET
                    );
                    boolean alignOperands = false;

                    if (operatorRule != null) {
                        ASTNode wordOperator = operatorRule.findChildByType(BOOLEAN_WORD_OPERATOR_TOKEN_SET);

                        if (wordOperator != null) {
                            alignOperands = codeStyleSettings.ALIGN_BOOLEAN_OPERANDS;
                        }
                    }

                    return alignOperands;
                },
                SOMETIMES_BOOLEAN_OPERATOR_RULE_TOKEN_SET
        );
    }

    /**
     * Builds stab.stabBody.*
     *
     * @param stabBody       a child of a `stab` in `doBlock`
     * @param childWrap      wrap for all child blocks in `stabBody`
     * @param childAlignment alignment to use for all child blocks of `stabBody`
     * @param childIndent    indent to use for all child blocks of the `stabBody`
     * @return children of stabBody that should be aligned together with the same indent
     */
    @NotNull
    private List<com.intellij.formatting.Block> buildStabBodyChildren(@NotNull ASTNode stabBody,
                                                                      @NotNull Wrap childWrap,
                                                                      @NotNull Alignment childAlignment,
                                                                      @NotNull Indent childIndent) {

        return buildChildren(
                stabBody,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blockList.addAll(buildAccessExpressionChildren(child, childWrap, childAlignment, childIndent));
                    } else if (childElementType == ElixirTypes.COMMENT) {
                        // COMMENTs don't use childWrap as they can either be at end-of-line or on their own line
                        blockList.add(buildChild(child, childAlignment, childIndent));
                    } else if (childElementType == END_OF_EXPRESSION) {
                        blockList.addAll(buildEndOfExpressionChildren(child, childAlignment, childIndent));
                    } else {
                        blockList.add(buildChild(child, childWrap, childAlignment, childIndent));
                    }

                    return blockList;
                }
        );
    }

    /**
     * Builds stab.*
     *
     * @param stab a child of a `doBlock`
     * @return children of stab that should be aligned together and indented normal relative to the call with the
     * doBlock.
     */
    @NotNull
    private List<com.intellij.formatting.Block> buildStabChildren(@NotNull CompositeElement stab,
                                                                  @Nullable Wrap stabBodyChildrenWrap) {
        /* all children share the same alignment as expressions inside a doBlock above the stab are assumed to be
           aligned on the left-side */
        Alignment childAlignment = Alignment.createAlignment();

        Indent childIndent = Indent.getNormalIndent(
                codeStyleSettings(stab).ALIGN_UNMATCHED_CALL_DO_BLOCKS ==
                        CodeStyleSettings.UnmatchedCallDoBlockAlignment.CALL.value
        );
        WrapType stabOperationWrapType;

        if (hasAtLeastCountChildren(stab, ElixirTypes.STAB_OPERATION, 2)) {
            stabOperationWrapType = WrapType.ALWAYS;
        } else {
            stabOperationWrapType = WrapType.NORMAL;
        }

        Wrap stabOperationWrap = Wrap.createWrap(stabOperationWrapType, true);

        if (stabBodyChildrenWrap == null) {
            stabBodyChildrenWrap = Wrap.createChildWrap(stabOperationWrap, WrapType.CHOP_DOWN_IF_LONG, true);
        }

        Wrap finalStabBodyChildrenWrap = stabBodyChildrenWrap;

        return buildChildren(
                stab,
                (child, childElementType, blockList) -> {
                    if (childElementType == END_OF_EXPRESSION) {
                        blockList.addAll(buildEndOfExpressionChildren(child, childAlignment, childIndent));
                    } else if (childElementType == ElixirTypes.STAB_BODY) {
                        blockList.addAll(
                                buildStabBodyChildren(
                                        child,
                                        finalStabBodyChildrenWrap,
                                        childAlignment,
                                        childIndent
                                )
                        );
                    } else {
                        blockList.add(
                                buildChild(
                                        child,
                                        stabOperationWrap,
                                        childIndent,
                                        finalStabBodyChildrenWrap
                                )
                        );
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildStabOperationChildren(
            @NotNull ASTNode stabOperation,
            @NotNull Wrap stabBodyChildrenWrap
    ) {
        return buildChildren(
                stabOperation,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.STAB_BODY) {
                        blockList.addAll(
                                buildStabBodyChildren(
                                        child,
                                        stabBodyChildrenWrap,
                                        Alignment.createAlignment(), // null,
                                        Indent.getNormalIndent(false)
                                )
                        );
                    } else if (childElementType == ElixirTypes.STAB_INFIX_OPERATOR) {
                        blockList.addAll(buildOperatorRuleChildren(child));
                    } else {
                        blockList.add(
                                buildChild(child)
                        );
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildTupleChildren(@NotNull ASTNode tuple) {
        return buildContainerChildren(tuple, ElixirTypes.OPENING_CURLY, null, ElixirTypes.CLOSING_CURLY);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildTwoOperationChildren(@NotNull ASTNode twoOperation) {
        //noinspection ConstantConditions
        return buildAlignedOperandsOperationChildren(
                twoOperation,
                (codeStyleSettings) -> codeStyleSettings.ALIGN_TWO_OPERANDS,
                ElixirTypes.TWO_INFIX_OPERATOR
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildTypeOperationChildren(@NotNull ASTNode typeOperation) {
        final Alignment[] operandAlignment = {null};

        return buildChildren(
                typeOperation,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blockList.addAll(buildAccessExpressionChildren(child, operandAlignment[0]));
                    } else if (childElementType == ElixirTypes.TYPE_INFIX_OPERATOR) {
                        blockList.addAll(buildOperatorRuleChildren(child));

                        if (codeStyleSettings(typeOperation).ALIGN_TYPE_DEFINITION_TO_RIGHT_OF_OPERATOR) {
                            operandAlignment[0] = Alignment.createAlignment();
                        }
                    } else {
                        blockList.add(buildChild(child, operandAlignment[0]));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildUnmatchedCallChildren(@NotNull ASTNode parentNode,
                                                                           @Nullable Wrap parentWrap,
                                                                           @Nullable Alignment parentAlignment) {
        return buildChildren(
                parentNode,
                (child, childElementType, blockList) -> {
                    /* the elements in the doBlock.stab must be direct children of the call, so that they can be
                       indented relative to parent */
                    if (childElementType == ElixirTypes.DO_BLOCK) {
                        blockList.addAll(buildDoBlockChildren(child, parentAlignment));
                    } else {
                        blockList.addAll(
                                buildCallChildChildren(child, childElementType, parentWrap, parentAlignment)
                        );
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildWhenOperationChildren(@NotNull ASTNode whenOperation) {
        Wrap operatorWrap = Wrap.createWrap(WrapType.NORMAL, true);

        final Alignment[] operandAlignment = {null};
        final Wrap[] operandWrap = {Wrap.createWrap(WrapType.NONE, false)};

        return buildChildren(
                whenOperation,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blockList.addAll(
                                buildAccessExpressionChildren(child, operandWrap[0], operandAlignment[0])
                        );
                    } else if (OPERATOR_RULE_TOKEN_SET.contains(childElementType)) {
                        blockList.addAll(buildOperatorRuleChildren(child, operatorWrap));
                        operandAlignment[0] = Alignment.createAlignment();
                        operandWrap[0] = Wrap.createChildWrap(operatorWrap, WrapType.NORMAL, true);
                    } else {
                        blockList.add(buildChild(child, operandWrap[0], operandAlignment[0]));
                    }

                    return blockList;
                }
        );
    }

    private CodeStyleSettings codeStyleSettings(@NotNull ASTNode operation) {
        return CodeStyleSettingsManager
                .getInstance(operation.getPsi().getProject())
                .getCurrentSettings()
                .getCustomSettings(CodeStyleSettings.class);
    }

    /**
     * @return {@code null} means to use the default indentation for the default formatter.  Any instance means use the
     * specified indent.
     */
    @Nullable
    @Override
    public Indent getIndent() {
        return indent;
    }

    /**
     * Must be set or Elixir settings won't be used
     *
     * @return current block's language (is used to decide on what code style settings should be used for it)
     */
    @NotNull
    @Override
    public Language getLanguage() {
        return ElixirLanguage.INSTANCE;
    }

    /**
     * Returns a spacing object indicating what spaces and/or line breaks are added between two
     * specified children of this block.
     *
     * @param child1 the first child for which spacing is requested;
     *               <code>null</code> if given <code>'child2'</code> block is the first document block
     * @param child2 the second child for which spacing is requested.
     * @return the spacing instance, or null if no special spacing is required. If null is returned,
     * the formatter does not insert or delete spaces between the child blocks, but may insert
     * a line break if the line wraps at the position between the child blocks.
     * @see Spacing#createSpacing(int, int, int, boolean, int)
     * @see Spacing#getReadOnlySpacing()
     */
    @Nullable
    @Override
    public Spacing getSpacing(@Nullable com.intellij.formatting.Block child1,
                              @NotNull com.intellij.formatting.Block child2) {
        Spacing spacing = null;

        // Prevent `& &1` from becoming `&&1`, which is parsed as `&& 1` with a missing left operand
        /* Prevent `& &1 + &2` from becoming `&&1 + &2`, which is parsed as `&& 1 + &2` with missing left operand for
           `&& 1` */
        // Prevent `_ && &1` from becoming `_ &&& 1`
        // Prevent `_ &&& &2` from becoming ` _ &&&& 2`, which has no meaning
        if (child1 instanceof ASTBlock && child2 instanceof ASTBlock) {
            ASTBlock child1ASTBlock = (ASTBlock) child1;
            ASTNode child1Node = child1ASTBlock.getNode();

            if (child1Node instanceof LeafPsiElement) {
                LeafPsiElement child1LeafPsiElement = (LeafPsiElement) child1Node;

                // capture (`&`) or and symbol operators (`&&` or `&&&`)
                if (child1LeafPsiElement.charAt(child1LeafPsiElement.getTextLength() - 1) == '&') {
                    ASTBlock child2ASTBlock = (ASTBlock) child2;
                    ASTNode firstLeafElementASTNode = child2ASTBlock.getNode().findLeafElementAt(0);

                    if (firstLeafElementASTNode != null &&
                            firstLeafElementASTNode instanceof LeafPsiElement &&
                            ((LeafPsiElement) firstLeafElementASTNode).charAt(0) == '&') {
                        spacing = Spacing.createSpacing(1, 1, 0, true, 0);
                    }
                }
            }
        }

        if (spacing == null) {
            spacing = spacingBuilder.getSpacing(this, child1, child2);
        }

        return spacing;
    }

    /**
     * Returns true if the specified block may not contain child blocks. Used as an optimization
     * to avoid building the complete formatting model through calls to {@link #getSubBlocks()}.
     *
     * @return true if the block is a leaf block and may not contain child blocks, false otherwise.
     */
    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

    /**
     * If already partially wrapped between {@code openingElementType} and {@code closingElementType}, then wrap all
     *
     * @return {@link WrapType#ALWAYS} if elements for {@code openingElementType} and {@code closingElementType} are on
     *   different lines; otherwise, {@link WrapType#CHOP_DOWN_IF_LONG}.
     */
    @NotNull
    private Wrap tailWrap(@NotNull ASTNode parent,
                          @NotNull IElementType openingElementType,
                          @NotNull IElementType closingElementType) {
        Document document = document(parent.getPsi());
        Wrap tailWrap = null;

        if (document != null) {
            ASTNode openingElement = parent.findChildByType(openingElementType);

            if (openingElement != null) {
                ASTNode closingElement = parent.findChildByType(closingElementType);

                if (closingElement != null) {
                    if (document.getLineNumber(openingElement.getStartOffset()) !=
                            document.getLineNumber(closingElement.getStartOffset())) {
                        tailWrap = Wrap.createWrap(WrapType.ALWAYS, true);
                    }

                }
            }
        }

        if (tailWrap == null) {
            tailWrap = Wrap.createWrap(WrapType.CHOP_DOWN_IF_LONG, true);
        }

        return tailWrap;
    }
}
