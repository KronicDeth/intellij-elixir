package org.elixir_lang.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.Function;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.intellij.openapi.util.Pair.pair;
import static org.elixir_lang.psi.ElixirTypes.END_OF_EXPRESSION;

/**
 * @note MUST implement {@link BlockEx} or language-specific indent settings will NOT be used and only the generic ones
 * will be used.
 */
public class Block extends AbstractBlock implements BlockEx {
    private static final TokenSet CAPTURE_NON_NUMERIC_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_CAPTURE_NON_NUMERIC_OPERATION,
            ElixirTypes.UNMATCHED_CAPTURE_NON_NUMERIC_OPERATION
    );
    private static final Map<IElementType, Boolean> IS_OPERATION_BY_ELEMENT_TYPE = new IdentityHashMap<>();
    private static final Map<IElementType, Boolean> IS_OPERATOR_RULE_BY_ELEMENT_TYPE = new IdentityHashMap<>();
    private static final Map<IElementType, Boolean> IS_UNMATCHED_CALL_BY_ELEMENT_TYPE = new IdentityHashMap<>();
    private static final TokenSet MULTIPLICATION_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_MULTIPLICATION_OPERATION,
            ElixirTypes.UNMATCHED_MULTIPLICATION_OPERATION
    );
    private static final TokenSet WHEN_OPERATION_TOKEN_SET = TokenSet.create(
            ElixirTypes.MATCHED_WHEN_OPERATION,
            ElixirTypes.UNMATCHED_WHEN_OPERATION
    );
    private static final TokenSet WHITESPACE_TOKEN_SET =
            TokenSet.create(ElixirTypes.EOL, TokenType.WHITE_SPACE, ElixirTypes.SIGNIFICANT_WHITE_SPACE);

    static {
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.MATCHED_ADDITION_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.MATCHED_CAPTURE_NON_NUMERIC_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.MATCHED_COMPARISON_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.MATCHED_IN_MATCH_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.MATCHED_MATCH_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.MATCHED_MULTIPLICATION_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.MATCHED_RELATIONAL_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.MATCHED_TYPE_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.MATCHED_UNARY_NON_NUMERIC_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.MATCHED_WHEN_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.UNARY_NUMERIC_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_ADDITION_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_CAPTURE_NON_NUMERIC_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_COMPARISON_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_IN_MATCH_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_MATCH_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_MULTIPLICATION_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_RELATIONAL_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_TYPE_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_UNARY_NON_NUMERIC_OPERATION, true);
        IS_OPERATION_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_WHEN_OPERATION, true);
    }

    static {
        IS_OPERATOR_RULE_BY_ELEMENT_TYPE.put(ElixirTypes.ADDITION_INFIX_OPERATOR, true);
        IS_OPERATOR_RULE_BY_ELEMENT_TYPE.put(ElixirTypes.CAPTURE_PREFIX_OPERATOR, true);
        IS_OPERATOR_RULE_BY_ELEMENT_TYPE.put(ElixirTypes.COMPARISON_INFIX_OPERATOR, true);
        IS_OPERATOR_RULE_BY_ELEMENT_TYPE.put(ElixirTypes.IN_MATCH_INFIX_OPERATOR, true);
        IS_OPERATOR_RULE_BY_ELEMENT_TYPE.put(ElixirTypes.MATCH_INFIX_OPERATOR, true);
        IS_OPERATOR_RULE_BY_ELEMENT_TYPE.put(ElixirTypes.MULTIPLICATION_INFIX_OPERATOR, true);
        IS_OPERATOR_RULE_BY_ELEMENT_TYPE.put(ElixirTypes.RELATIONAL_INFIX_OPERATOR, true);
        IS_OPERATOR_RULE_BY_ELEMENT_TYPE.put(ElixirTypes.STAB_INFIX_OPERATOR, true);
        IS_OPERATOR_RULE_BY_ELEMENT_TYPE.put(ElixirTypes.TYPE_INFIX_OPERATOR, true);
        IS_OPERATOR_RULE_BY_ELEMENT_TYPE.put(ElixirTypes.UNARY_PREFIX_OPERATOR, true);
        IS_OPERATOR_RULE_BY_ELEMENT_TYPE.put(ElixirTypes.WHEN_INFIX_OPERATOR, true);
    }

    static {
        IS_UNMATCHED_CALL_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_AT_UNQUALIFIED_NO_PARENTHESES_CALL, true);
        IS_UNMATCHED_CALL_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_DOT_CALL, true);
        IS_UNMATCHED_CALL_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_QUALIFIED_NO_ARGUMENTS_CALL, true);
        IS_UNMATCHED_CALL_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_QUALIFIED_NO_PARENTHESES_CALL, true);
        IS_UNMATCHED_CALL_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_QUALIFIED_PARENTHESES_CALL, true);
        IS_UNMATCHED_CALL_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_UNQUALIFIED_NO_ARGUMENTS_CALL, true);
        IS_UNMATCHED_CALL_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_UNQUALIFIED_NO_PARENTHESES_CALL, true);
        IS_UNMATCHED_CALL_BY_ELEMENT_TYPE.put(ElixirTypes.UNMATCHED_UNQUALIFIED_PARENTHESES_CALL, true);
    }

    @Nullable
    private final Wrap childrenWrap;
    private final Indent indent;
    private final SpacingBuilder spacingBuilder;

    public Block(@NotNull ASTNode node,
                 @Nullable Wrap wrap,
                 @Nullable Alignment alignment,
                 @NotNull SpacingBuilder spacingBuilder) {
        this(node, wrap, alignment, spacingBuilder, Indent.getNoneIndent());
    }

    public Block(@NotNull ASTNode node,
                 @Nullable Wrap wrap,
                 @Nullable Alignment alignment,
                 @NotNull SpacingBuilder spacingBuilder,
                 @NotNull Indent indent) {
        this(node, wrap, alignment, spacingBuilder, indent, null);
    }

    public Block(@NotNull ASTNode node,
                 @Nullable Wrap wrap,
                 @Nullable Alignment alignment,
                 @NotNull SpacingBuilder spacingBuilder,
                 @NotNull Indent indent,
                 @Nullable Wrap childrenWrap) {
        super(node, wrap, alignment);
        this.spacingBuilder = spacingBuilder;
        this.indent = indent;
        this.childrenWrap = childrenWrap;
    }

    private static boolean isOperationElementType(IElementType elementType) {
        return IS_OPERATION_BY_ELEMENT_TYPE.containsKey(elementType);
    }

    private static boolean isOperatorRuleElementType(IElementType elementType) {
        return IS_OPERATOR_RULE_BY_ELEMENT_TYPE.containsKey(elementType);
    }

    private static boolean isUnmatchedCallElementType(IElementType elementType) {
        return IS_UNMATCHED_CALL_BY_ELEMENT_TYPE.containsKey(elementType);
    }

    private static boolean shouldBuildBlock(@NotNull IElementType childElementType) {
        return !WHITESPACE_TOKEN_SET.contains(childElementType);
    }

    private static boolean shouldBuildBlock(@NotNull ASTNode child) {
        return shouldBuildBlock(child.getElementType());
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildAccessExpressionChildren(
            @NotNull ASTNode accessExpression,
            @NotNull Alignment childrenAlignment
    ) {
        return buildAccessExpressionChildren(
                accessExpression,
                Wrap.createWrap(WrapType.NORMAL, true),
                childrenAlignment
        );
    }

    private @NotNull List<com.intellij.formatting.Block> buildAccessExpressionChildren(
            @NotNull ASTNode accessExpression,
            @Nullable Wrap childrenWrap,
            @NotNull Alignment childrenAlignment
    ) {
        return buildChildren(
                accessExpression,
                (childBlockListPair) -> {
                    ASTNode child = childBlockListPair.first;
                    List<com.intellij.formatting.Block> blockList = childBlockListPair.second;

                    blockList.add(buildChild(child, childrenWrap, childrenAlignment));

                    return blockList;
                }
        );
    }

    /**
     * Builds anonymousFunction FN, stab, and END as siblings.  If it is a one-liner, the END is setup to wrap once
     *   all other part wraps, such as due to chopping.
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
                myNode,
                (childBlockListPair) -> {
                    ASTNode child = childBlockListPair.first;
                    IElementType childElementType = child.getElementType();

                    List<com.intellij.formatting.Block> lambdaBlocks = childBlockListPair.second;

                    if (childElementType == ElixirTypes.END) {
                        //noinspection ConstantConditions
                        lambdaBlocks.add(buildChild(child, endWrap, myAlignment, Indent.getNoneIndent()));
                    } else if (childElementType == END_OF_EXPRESSION) {
                        lambdaBlocks.addAll(
                                buildEndOfExpressionChildren(child, childrenAlignment, Indent.getNoneIndent())
                        );
                    } else if (childElementType == ElixirTypes.STAB) {
                        lambdaBlocks.addAll(buildStabChildren((CompositeElement) child, stabBodyChildrenWrap));
                    } else {
                        lambdaBlocks.add(buildChild(child, childrenAlignment));
                    }

                    return lambdaBlocks;
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
                (childBlockListPair) -> {
                    ASTNode child = childBlockListPair.first;
                    IElementType childElementType = child.getElementType();

                    List<com.intellij.formatting.Block> blockList = childBlockListPair.second;

                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blockList.addAll(buildAccessExpressionChildren(child, Alignment.createAlignment()));
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

                    if (leftOperandElementType == ElixirTypes.UNMATCHED_UNQUALIFIED_NO_ARGUMENTS_CALL) {
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
        return buildChild(child, Alignment.createAlignment());
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child, @NotNull Alignment alignment) {
        return buildChild(child, alignment, Indent.getNoneIndent());
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child, @NotNull Wrap wrap, @NotNull Alignment alignment) {
        return buildChild(
                child,
                wrap,
                alignment,
                Indent.getNoneIndent()
        );
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child, @NotNull Alignment alignment, @NotNull Indent indent) {
        return buildChild(child,
                Wrap.createWrap(WrapType.NONE, false),
                alignment,
                indent
        );
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child,
                             @NotNull Wrap wrap,
                             @NotNull Alignment alignment,
                             @NotNull Indent indent) {
        return buildChild(
                child,
                wrap,
                alignment,
                indent,
                null
        );
    }

    @NotNull
    private Block buildChild(@NotNull ASTNode child,
                             @NotNull Wrap wrap,
                             @NotNull Indent indent,
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
    private Block buildChild(@NotNull ASTNode child,
                             @NotNull Wrap wrap,
                             @NotNull Alignment alignment,
                             @NotNull Indent indent,
                             @Nullable Wrap childrenWrap) {
        return new Block(
                child,
                wrap,
                alignment,
                spacingBuilder,
                indent,
                childrenWrap
        );
    }

    @Override
    protected List<com.intellij.formatting.Block> buildChildren() {
        List<com.intellij.formatting.Block> blocks;
        // shared so that children are all aligned as alignment is shared based on sharing same alignment instance
        IElementType elementType = myNode.getElementType();

        if (elementType == ElixirTypes.ANONYMOUS_FUNCTION) {
            blocks = buildAnonymousFunctionChildren(myNode);
        } else if (CAPTURE_NON_NUMERIC_OPERATION_TOKEN_SET.contains(elementType)) {
            blocks = buildCaptureNonNumericOperationChildren(myNode);
        } else if (elementType == ElixirTypes.STAB_OPERATION) {
            //noinspection ConstantConditions
            blocks = buildStabOperationChildren(myNode, childrenWrap);
        } else if (WHEN_OPERATION_TOKEN_SET.contains(elementType)) {
            blocks = buildWhenOperationChildren(myNode);
        } else if (isOperationElementType(elementType)) {
            blocks = buildOperationChildren(myNode);
        } else if (isUnmatchedCallElementType(elementType)) {
            blocks = buildUnmatchedCallChildren();
        } else {
            final Alignment childrenAlignment = Alignment.createAlignment();

            blocks = buildChildren(
                    myNode,
                    (childBlockListPair) -> {
                        ASTNode child = childBlockListPair.first;
                        List<com.intellij.formatting.Block> lambdaBlocks = childBlockListPair.second;

                        IElementType childElementType = child.getElementType();

                        if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                            lambdaBlocks.addAll(
                                    buildAccessExpressionChildren(child, childrenAlignment)
                            );
                        } else if (childElementType == END_OF_EXPRESSION) {
                            lambdaBlocks.addAll(
                                    buildEndOfExpressionChildren(child, childrenAlignment, Indent.getNoneIndent())
                            );
                        } else if (childElementType == ElixirTypes.WHEN_INFIX_OPERATOR) {
                            lambdaBlocks.addAll(buildOperatorRuleChildren(child));
                        } else {
                            Block block = new Block(
                                    child,
                                    Wrap.createWrap(WrapType.NONE, false),
                                    childrenAlignment,
                                    spacingBuilder
                            );
                            lambdaBlocks.add(block);
                        }

                        return lambdaBlocks;
                    }
            );
        }

        return blocks;
    }

    private @NotNull
    List<com.intellij.formatting.Block> buildChildren(
            @NotNull ASTNode node,
            @NotNull Function<Pair<ASTNode, List<com.intellij.formatting.Block>>,
                    List<com.intellij.formatting.Block>> buildChildBlocks) {
        List<com.intellij.formatting.Block> blocks = new ArrayList<>();

        ASTNode child = node.getFirstChildNode();

        while (child != null) {
            if (shouldBuildBlock(child)) {
                blocks = buildChildBlocks.fun(pair(child, blocks));
            }

            child = child.getTreeNext();
        }

        return blocks;
    }

    /**
     * Builds doBlock DO, stab.*, and END as siblings, so they can all be indented relative to the parent unmatched call
     * from {@link #buildUnmatchedCallChildren()}
     *
     * @param doBlock doBlock that is a child of an unmatched call, but needs to be flattened for formatting
     * @return the flattened children of the doBlock: DO, stab.*, and END.
     */
    @NotNull
    private List<com.intellij.formatting.Block> buildDoBlockChildren(@NotNull ASTNode doBlock) {
        return buildChildren(
                doBlock,
                (childBlockListPair) -> {
                    ASTNode child = childBlockListPair.first;
                    IElementType childElementType = child.getElementType();
                    List<com.intellij.formatting.Block> blocks = childBlockListPair.second;

                    if (childElementType == END_OF_EXPRESSION) {
                        blocks.addAll(
                                buildEndOfExpressionChildren(
                                        child,
                                        Alignment.createAlignment(),
                                        Indent.getNoneIndent()
                                )
                        );
                    } else if (childElementType == ElixirTypes.STAB) {
                        blocks.addAll(buildStabChildren((CompositeElement) child));
                    } else {
                        Alignment childAlignment;

                        if (childElementType == ElixirTypes.END) {
                            childAlignment = myAlignment;
                        } else {
                            childAlignment = Alignment.createAlignment();
                        }

                        //noinspection ConstantConditions
                        blocks.add(buildChild(child, childAlignment));
                    }

                    return blocks;
                }
        );
    }

    /**
     * Builds endOfExpression.*.  Importantly, it separates out the EOLs, which are whitespace from the comments that
     * may be interlaced
     */
    @NotNull
    private List<com.intellij.formatting.Block> buildEndOfExpressionChildren(@NotNull ASTNode endOfExpression,
                                                                             @NotNull Alignment childAlignment,
                                                                             @NotNull Indent childIndent) {
        return buildChildren(
                endOfExpression,
                childBlockListPair -> {
                    ASTNode child = childBlockListPair.first;
                    List<com.intellij.formatting.Block> blocks = childBlockListPair.second;

                    blocks.add(buildChild(child, childAlignment, childIndent));

                    return blocks;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildOperationChildren(ASTNode operation) {
        return buildChildren(
                operation,
                (childBlockListPair) -> {
                    ASTNode child = childBlockListPair.first;
                    IElementType childElementType = child.getElementType();

                    List<com.intellij.formatting.Block> blocks = childBlockListPair.second;

                    /* Move the operator token ASTNode up, out of the operator rule ASTNode as the operator rule ASTNode
                       is only there to consume EOLs around the operator token ASTNode and EOLs will ignored */
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blocks.addAll(
                                buildAccessExpressionChildren(child, Alignment.createAlignment())
                        );
                    } else if (isOperatorRuleElementType(childElementType)) {
                        blocks.addAll(buildOperatorRuleChildren(child));
                    } else {
                        blocks.add(buildChild(child, Alignment.createAlignment()));
                    }

                    return blocks;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildOperatorRuleChildren(@NotNull ASTNode operatorRuleNode) {
        return buildOperatorRuleChildren(operatorRuleNode, Wrap.createWrap(WrapType.NORMAL, true));
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildOperatorRuleChildren(
            @NotNull ASTNode operatorRuleNode,
            @NotNull Wrap operatorWrap
    ) {
        return buildChildren(
                operatorRuleNode,
                (childBlockListPair) -> {
                    ASTNode child = childBlockListPair.first;
                    List<com.intellij.formatting.Block> blocks = childBlockListPair.second;

                    blocks.add(buildChild(child, operatorWrap, Alignment.createAlignment()));

                    return blocks;
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
                (childBlockListPair) -> {
                    ASTNode child = childBlockListPair.first;
                    IElementType childElementType = child.getElementType();
                    List<com.intellij.formatting.Block> blockList = childBlockListPair.second;

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

        if (stab.countChildren(TokenSet.create(ElixirTypes.STAB_OPERATION)) > 1) {
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
                (childBlockListPair) -> {
                    ASTNode child = childBlockListPair.first;
                    IElementType childElementType = child.getElementType();
                    List<com.intellij.formatting.Block> blocks = childBlockListPair.second;

                    if (childElementType == END_OF_EXPRESSION) {
                        blocks.addAll(buildEndOfExpressionChildren(child, childAlignment, childIndent));
                    } else if (childElementType == ElixirTypes.STAB_BODY) {
                        blocks.addAll(
                                buildStabBodyChildren(
                                        child,
                                        Wrap.createWrap(WrapType.ALWAYS, true),
                                        childAlignment,
                                        childIndent
                                )
                        );
                    } else {
                        blocks.add(
                                buildChild(
                                        child,
                                        stabOperationWrap,
                                        childIndent,
                                        finalStabBodyChildrenWrap
                                )
                        );
                    }

                    return blocks;
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
                (childBlockListPair) -> {
                    ASTNode child = childBlockListPair.first;
                    IElementType childElementType = child.getElementType();
                    List<com.intellij.formatting.Block> blockList = childBlockListPair.second;

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
                                buildChild(
                                        child,
                                        Alignment.createAlignment()
                                )
                        );
                    }

                    return blockList;
                }
        );
    }

    @NotNull
    private List<com.intellij.formatting.Block> buildUnmatchedCallChildren() {
        return buildChildren(
                myNode,
                (childBlockListPair) -> {
                    ASTNode child = childBlockListPair.first;
                    List<com.intellij.formatting.Block> blocks = childBlockListPair.second;

                    /* the elements in the doBlock.stab must be direct children of the call, so that they can be
                       indented relative to parent */
                    if (child.getElementType() == ElixirTypes.DO_BLOCK) {
                        blocks.addAll(buildDoBlockChildren(child));
                    } else {
                        blocks.add(buildChild(child, Alignment.createAlignment()));
                    }

                    return blocks;
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
                (childBlockListPair) -> {
                    ASTNode child = childBlockListPair.first;
                    IElementType childElementType = child.getElementType();

                    List<com.intellij.formatting.Block> blockList = childBlockListPair.second;

                    /* Move the operator token ASTNode up, out of the operator rule ASTNode as the operator rule ASTNode
                       is only there to consume EOLs around the operator token ASTNode and EOLs will ignored */
                    if (childElementType == ElixirTypes.ACCESS_EXPRESSION) {
                        blockList.addAll(
                                buildAccessExpressionChildren(child, childWrapRef.get(), Alignment.createAlignment())
                        );
                    } else if (isOperatorRuleElementType(childElementType)) {
                        blockList.addAll(buildOperatorRuleChildren(child, operatorWrap));
                        childWrapRef.set(rightOperandWrap);
                    } else {
                        blockList.add(buildChild(child, childWrapRef.get(), Alignment.createAlignment()));
                    }

                    return blockList;
                }
        );
    }

    @NotNull
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
        if (child1 instanceof ASTBlock && child2 instanceof ASTBlock) {
            ASTBlock child1ASTBlock = (ASTBlock) child1;

            if (child1ASTBlock.getNode().getElementType() == ElixirTypes.CAPTURE_OPERATOR) {
                ASTBlock child2ASTBlock = (ASTBlock) child2;
                ASTNode firstLeafElementASTNode = child2ASTBlock.getNode().findLeafElementAt(0);

                if (firstLeafElementASTNode != null &&
                        firstLeafElementASTNode.getElementType() == ElixirTypes.CAPTURE_OPERATOR) {
                    spacing = Spacing.createSpacing(1, 1, 0, true, 0);
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
}
