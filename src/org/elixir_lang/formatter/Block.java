package org.elixir_lang.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.elixir_lang.psi.ElixirTypes.*;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.lineNumber;

/**
 * @note MUST implement {@link BlockEx} or language-specific indent settings will NOT be used and only the generic ones
 * will be used.
 */
public class Block extends AbstractBlock implements BlockEx {
    private static final TokenSet CAPTURE_NON_NUMERIC_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_CAPTURE_NON_NUMERIC_OPERATION,
            ElixirTypes.UNMATCHED_CAPTURE_NON_NUMERIC_OPERATION
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
    private static final TokenSet MAP_TOKEN_SET = TokenSet.create(
            ElixirTypes.MAP_OPERATION,
            ElixirTypes.STRUCT_OPERATION
    );
    private static final TokenSet MULTIPLICATION_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_MULTIPLICATION_OPERATION,
            ElixirTypes.UNMATCHED_MULTIPLICATION_OPERATION
    );
    private static final TokenSet OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_ADDITION_OPERATION,
            ElixirTypes.MATCHED_AND_OPERATION,
            ElixirTypes.MATCHED_ARROW_OPERATION,
            ElixirTypes.MATCHED_CAPTURE_NON_NUMERIC_OPERATION,
            ElixirTypes.MATCHED_COMPARISON_OPERATION,
            ElixirTypes.MATCHED_IN_MATCH_OPERATION,
            ElixirTypes.MATCHED_IN_OPERATION,
            ElixirTypes.MATCHED_MATCH_OPERATION,
            ElixirTypes.MATCHED_MULTIPLICATION_OPERATION,
            ElixirTypes.MATCHED_OR_OPERATION,
            ElixirTypes.MATCHED_PIPE_OPERATION,
            ElixirTypes.MATCHED_RELATIONAL_OPERATION,
            ElixirTypes.MATCHED_THREE_OPERATION,
            ElixirTypes.MATCHED_TWO_OPERATION,
            ElixirTypes.MATCHED_TYPE_OPERATION,
            ElixirTypes.MATCHED_UNARY_NON_NUMERIC_OPERATION,
            ElixirTypes.MATCHED_WHEN_OPERATION,
            ElixirTypes.UNARY_NUMERIC_OPERATION,
            ElixirTypes.UNMATCHED_ADDITION_OPERATION,
            ElixirTypes.UNMATCHED_AND_OPERATION,
            ElixirTypes.UNMATCHED_ARROW_OPERATION,
            ElixirTypes.UNMATCHED_CAPTURE_NON_NUMERIC_OPERATION,
            ElixirTypes.UNMATCHED_COMPARISON_OPERATION,
            ElixirTypes.UNMATCHED_IN_MATCH_OPERATION,
            ElixirTypes.UNMATCHED_IN_OPERATION,
            ElixirTypes.UNMATCHED_MATCH_OPERATION,
            ElixirTypes.UNMATCHED_MULTIPLICATION_OPERATION,
            ElixirTypes.UNMATCHED_OR_OPERATION,
            ElixirTypes.UNMATCHED_PIPE_OPERATION,
            ElixirTypes.UNMATCHED_RELATIONAL_OPERATION,
            ElixirTypes.UNMATCHED_THREE_OPERATION,
            ElixirTypes.UNMATCHED_TWO_OPERATION,
            ElixirTypes.UNMATCHED_TYPE_OPERATION,
            ElixirTypes.UNMATCHED_UNARY_NON_NUMERIC_OPERATION,
            ElixirTypes.UNMATCHED_WHEN_OPERATION
    );
    private static final TokenSet OPERATOR_RULE_TOKEN_SET = TokenSet.create(
            ElixirTypes.ADDITION_INFIX_OPERATOR,
            ElixirTypes.AND_INFIX_OPERATOR,
            ElixirTypes.ARROW_INFIX_OPERATOR,
            ElixirTypes.AT_PREFIX_OPERATOR,
            ElixirTypes.CAPTURE_PREFIX_OPERATOR,
            ElixirTypes.COMPARISON_INFIX_OPERATOR,
            ElixirTypes.DOT_INFIX_OPERATOR,
            ElixirTypes.IN_INFIX_OPERATOR,
            ElixirTypes.IN_MATCH_INFIX_OPERATOR,
            ElixirTypes.MATCH_INFIX_OPERATOR,
            ElixirTypes.MULTIPLICATION_INFIX_OPERATOR,
            ElixirTypes.OR_INFIX_OPERATOR,
            ElixirTypes.PIPE_INFIX_OPERATOR,
            ElixirTypes.RELATIONAL_INFIX_OPERATOR,
            ElixirTypes.STAB_INFIX_OPERATOR,
            ElixirTypes.THREE_INFIX_OPERATOR,
            ElixirTypes.TWO_INFIX_OPERATOR,
            ElixirTypes.TYPE_INFIX_OPERATOR,
            ElixirTypes.UNARY_PREFIX_OPERATOR,
            ElixirTypes.WHEN_INFIX_OPERATOR
    );
    private static final TokenSet UNINDENTED_ONLY_ARGUMENT_TOKEN_SET = TokenSet.orSet(
            TokenSet.create(ElixirTypes.ANONYMOUS_FUNCTION),
            HEREDOC_TOKEN_SET
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
    private static final TokenSet UNMATCHED_NO_ARGUMENTS_CALL_TOKEN_SET = TokenSet.create(
            ElixirTypes.UNMATCHED_QUALIFIED_NO_ARGUMENTS_CALL,
            ElixirTypes.UNMATCHED_UNQUALIFIED_NO_ARGUMENTS_CALL
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

        if (MAP_TOKEN_SET.contains(node.getElementType())) {
            assert wrap != null : "mapOperation and structOperation must have a Wrap, so child wrap can be " +
                    "created for opening curly ({) and mapExpression for structOperation";
            assert alignment != null : "mapOperation and structOperation must have an Alignment, so that " +
                    "closing curly ({) can align to start of map";
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
        return buildAccessExpressionChildren(accessExpression, null, null);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAccessExpressionChildren(@NotNull ASTNode accessExpression,
                                                                              @Nullable Alignment childrenAlignment) {
        return buildAccessExpressionChildren(accessExpression, null, childrenAlignment);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAccessExpressionChildren(@NotNull ASTNode accessExpression,
                                                                              @Nullable Wrap childrenWrap) {
        return buildAccessExpressionChildren(accessExpression, childrenWrap, null);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAccessExpressionChildren(
            @NotNull ASTNode accessExpression,
            @Nullable Wrap childrenWrap,
            @Nullable Alignment childrenAlignment
    ) {
        return buildChildren(
                accessExpression,
                (child, childElementType, blockList) -> {
                    if (MAP_TOKEN_SET.contains(childElementType)) {
                        blockList.add(buildMapChild(child, childrenWrap, childrenAlignment));
                    } else {
                        blockList.add(buildChild(child, childrenWrap, childrenAlignment));
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
    private List<com.intellij.formatting.Block> buildAnonymousFunctionChildren(
            @NotNull ASTNode anonymousFunction,
            @Nullable Alignment anonymousFunctionAlignment
    ) {
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
                        //noinspection ConstantConditions
                        blockList.add(buildChild(child, endWrap, null, Indent.getNoneIndent()));
                    } else if (childElementType == END_OF_EXPRESSION) {
                        blockList.addAll(
                                buildEndOfExpressionChildren(child, childrenAlignment, Indent.getNoneIndent())
                        );
                    } else if (childElementType == ElixirTypes.STAB) {
                        blockList.addAll(buildStabChildren((CompositeElement) child, stabBodyChildrenWrap));
                    } else {
                        blockList.add(buildChild(child/*, childrenAlignment*/));
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
                        blockList.addAll(buildStabChildren((CompositeElement) child));
                    } else {
                        blockList.add(buildChild(child));
                    }

                    return blockList;
                }
        );
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

                    if (UNMATCHED_NO_ARGUMENTS_CALL_TOKEN_SET.contains(leftOperandElementType)) {
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
                                                              @Nullable Alignment parentAlignment) {
        List<com.intellij.formatting.Block> blocks;
        IElementType parentElementType = parent.getElementType();

        if (parentElementType == ElixirTypes.ANONYMOUS_FUNCTION) {
            blocks = buildAnonymousFunctionChildren(parent, parentAlignment);
        } else if (parentElementType == ElixirTypes.BLOCK_ITEM) {
            blocks = buildBlockItemChildren(parent, parentAlignment);
        } else if (CAPTURE_NON_NUMERIC_OPERATION_TOKEN_SET.contains(parentElementType)) {
            blocks = buildCaptureNonNumericOperationChildren(parent);
        } else if (HEREDOC_TOKEN_SET.contains(parentElementType)) {
            blocks = buildHeredocChildren((CompositeElement) parent);
        } else if (MAP_TOKEN_SET.contains(parentElementType)) {
            assert parentWrap != null : "mapOperation and structOperation must have a Wrap, so child wrap can be " +
                                        "created for opening curly ({) and mapExpression for structOperation";
            assert parentAlignment != null : "mapOperation and structOperation must have an Alignment, so that " +
                                             "closing curly ({) can align to start of map";

            blocks = buildMapChildren(parent, parentWrap, parentAlignment);
        } else if (parentElementType == ElixirTypes.MAP_UPDATE_ARGUMENTS) {
            blocks = buildMapUpdateArgumentsChildren(parent);
        } else if (parentElementType == ElixirTypes.STAB_OPERATION) {
            //noinspection ConstantConditions
            blocks = buildStabOperationChildren(parent, childrenWrap);
        } else if (WHEN_OPERATION_TOKEN_SET.contains(parentElementType)) {
            blocks = buildWhenOperationChildren(parent);
        } else if (OPERATION_TOKEN_SET.contains(parentElementType)) {
            blocks = buildOperationChildren(parent);
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
                        } else if (MAP_TOKEN_SET.contains(childElementType)) {
                            blockList.add(buildMapChild(child, childrenWrap, childrenAlignment));
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
        return buildChildren(myNode, myWrap, myAlignment);
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
                        //noinspection ConstantConditions
                        blockList.add(buildChild(child, parentAlignment));
                    } else if (childElementType == ElixirTypes.END) {
                        Wrap endWrap;

                        if (hasBLockLists) {
                            endWrap = Wrap.createWrap(WrapType.ALWAYS, true);
                        } else {
                            endWrap = Wrap.createWrap(WrapType.NORMAL, true);
                        }

                        //noinspection ConstantConditions
                        blockList.add(buildChild(child, endWrap, parentAlignment));
                    } else if (childElementType == END_OF_EXPRESSION) {
                        blockList.addAll(
                                buildEndOfExpressionChildren(child, null, Indent.getNoneIndent())
                        );
                    } else if (childElementType == ElixirTypes.STAB) {
                        blockList.addAll(buildStabChildren((CompositeElement) child));
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
    private List<com.intellij.formatting.Block> buildMapArgumentsChildren(@NotNull ASTNode mapArguments,
                                                                          @NotNull Wrap mapChildWrap,
                                                                          @NotNull Alignment mapAlignment) {
        Wrap tailWrap = tailWrap(mapArguments, ElixirTypes.OPENING_CURLY, ElixirTypes.CLOSING_CURLY);

        return buildChildren(
                mapArguments,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.CLOSING_CURLY) {
                        blockList.add(buildChild(child, tailWrap, mapAlignment, Indent.getNoneIndent()));
                    } else if (childElementType == ElixirTypes.MAP_CONSTRUCTION_ARGUMENTS) {
                        blockList.addAll(buildMapConstructArgumentsChildren(child, tailWrap));
                    } else if (childElementType == ElixirTypes.OPENING_CURLY) {
                        blockList.add(
                                buildChild(child, mapChildWrap)
                        );
                    } else {
                        blockList.add(buildChild(child, tailWrap, Indent.getNormalIndent()));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private com.intellij.formatting.Block buildMapChild(@NotNull ASTNode map,
                                                        @Nullable Wrap childrenWrap,
                                                        @Nullable Alignment childrenAlignment) {
        Wrap mapWrap;

        if (childrenWrap != null) {
            mapWrap = childrenWrap;
        } else {
            mapWrap = Wrap.createWrap(WrapType.NORMAL, true);
        }

        Alignment mapAlignment;

        if (childrenAlignment != null) {
            mapAlignment = childrenAlignment;
        } else {
            mapAlignment = Alignment.createAlignment();
        }

        return buildChild(map, mapWrap, mapAlignment);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildMapChildren(@NotNull ASTNode map,
                                                                 @NotNull Wrap mapWrap,
                                                                 @NotNull Alignment mapAlignment) {
        Wrap mapChildWrap = Wrap.createChildWrap(mapWrap, WrapType.NORMAL, true);

        return buildChildren(
                map,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.MAP_ARGUMENTS) {
                        // pass mapAlignment for CLOSING_CURLY alignment
                        blockList.addAll(buildMapArgumentsChildren(child, mapChildWrap, mapAlignment));
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
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildMapUpdateArgumentsChildren(ASTNode mapUpdateArguments) {
        return buildChildren(
                mapUpdateArguments,
                (child, childElementType, blockList) -> {
                    if (childElementType == ElixirTypes.PIPE_INFIX_OPERATOR) {
                        blockList.addAll(buildOperatorRuleChildren(child));
                    } else {
                        blockList.add(buildChild(child));
                    }

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

    @NotNull
    private List<com.intellij.formatting.Block> buildNoParenthesesOneArgument(
            @NotNull ASTNode child,
            @Nullable Wrap parentWrap,
            @Nullable Alignment parentAlignment
    ) {
        List<com.intellij.formatting.Block> blockList = buildChildren(child, parentWrap, parentAlignment);

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
    private List<com.intellij.formatting.Block> buildOperatorRuleChildren(@NotNull ASTNode operatorRuleNode) {
        return buildOperatorRuleChildren(operatorRuleNode, null);
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildOperatorRuleChildren(
            @NotNull ASTNode operatorRuleNode,
            @Nullable Wrap operatorWrap
    ) {
        return buildChildren(
                operatorRuleNode,
                (child, childElementType, blockList) -> {
                    blockList.add(buildChild(child, operatorWrap, Indent.getNoneIndent()));

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
                    if (childElementType == ElixirTypes.CLOSING_PARENTHESIS) {
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
                    if (childElementType == END_OF_EXPRESSION) {
                        blockList.addAll(buildEndOfExpressionChildren(child, childAlignment, childIndent));
                    } else {
                        blockList.add(buildChild(child, childWrap, childAlignment, childIndent));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildStabChildren(@NotNull CompositeElement stab) {
        return buildStabChildren(stab, null);
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

        Indent childIndent = Indent.getNormalIndent(true);
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
                                        Wrap.createWrap(WrapType.ALWAYS, true),
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
                    } else if (childElementType == ElixirTypes.MATCHED_PARENTHESES_ARGUMENTS) {
                        blockList.addAll(buildMatchedParenthesesArguments(child, parentWrap, parentAlignment));
                    } else if (childElementType == ElixirTypes.NO_PARENTHESES_ONE_ARGUMENT) {
                        blockList.addAll(buildNoParenthesesOneArgument(child, parentWrap, parentAlignment));
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
    private List<com.intellij.formatting.Block> buildWhenOperationChildren(@NotNull ASTNode whenOperation) {
        Wrap operatorWrap = Wrap.createWrap(WrapType.NORMAL, true);
        Wrap rightOperandWrap = Wrap.createChildWrap(operatorWrap, WrapType.NORMAL, true);
        Ref<Wrap> childWrapRef = Ref.create(Wrap.createWrap(WrapType.NONE, false));

        return buildChildren(
                whenOperation,
                (child, childElementType, blockList) -> {
                    /* Move the operator token ASTNode up, out of the operator rule ASTNode as the operator rule ASTNode
                       is only there to consume EOLs around the operator token ASTNode and EOLs will ignored */
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blockList.addAll(
                                buildAccessExpressionChildren(child, childWrapRef.get())
                        );
                    } else if (OPERATOR_RULE_TOKEN_SET.contains(childElementType)) {
                        blockList.addAll(buildOperatorRuleChildren(child, operatorWrap));
                        childWrapRef.set(rightOperandWrap);
                    } else {
                        blockList.add(buildChild(child, childWrapRef.get()));
                    }

                    return blockList;
                }
        );
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
        Wrap tailWrap;

        if (lineNumber(parent.findChildByType(openingElementType)) !=
                lineNumber(parent.findChildByType(closingElementType))) {
            tailWrap = Wrap.createWrap(WrapType.ALWAYS, true);
        } else {
            tailWrap = Wrap.createWrap(WrapType.CHOP_DOWN_IF_LONG, true);
        }

        return tailWrap;
    }
}
