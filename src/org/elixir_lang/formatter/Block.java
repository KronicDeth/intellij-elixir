package org.elixir_lang.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * @note MUST implement {@link BlockEx} or language-specific indent settings will NOT be used and only the generic ones
 *   will be used.
 */
public class Block extends AbstractBlock implements BlockEx {
    private final SpacingBuilder spacingBuilder;
    private final Indent indent;

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
        super(node, wrap, alignment);
        this.spacingBuilder = spacingBuilder;
        this.indent = indent;
    }
    @Override
    protected List<com.intellij.formatting.Block> buildChildren() {
        List<com.intellij.formatting.Block> blocks;
        // shared so that children are all aligned as alignment is shared based on sharing same alignment instance
        Alignment childrenAlignment = null;
        IElementType elementType = myNode.getElementType();

        if (isUnmatchedCallElementType(elementType)) {
            blocks = buildUnmatchedCallChildren();
        } else {
            blocks = new ArrayList<>();
            ASTNode child = myNode.getFirstChildNode();

            while (child != null) {
                if (shouldBuildBlock(child)) {
                    if (childrenAlignment == null) {
                        childrenAlignment = Alignment.createAlignment();
                    }

                    Block block = new Block(
                            child,
                            Wrap.createWrap(WrapType.NONE, false),
                            childrenAlignment,
                            spacingBuilder
                    );
                    blocks.add(block);
                }

                child = child.getTreeNext();
            }
        }

        return blocks;
    }

    @NotNull
    @Override
    public Indent getIndent() {
        return indent;
    }

    /**
     * Must be set or Elixir settings won't be used
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
        return spacingBuilder.getSpacing(this, child1, child2);
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

    @NotNull
    private List<com.intellij.formatting.Block> buildUnmatchedCallChildren() {
        List<com.intellij.formatting.Block> blocks = new ArrayList<>();

        ASTNode child = myNode.getFirstChildNode();

        while (child != null) {
            IElementType childElementType = child.getElementType();

            if (shouldBuildBlock(child, childElementType)) {
                /* the elements in the doBlock.stab must be direct children of the call, so that they can be indented
                   relative to parent */
                if (childElementType == ElixirTypes.DO_BLOCK) {
                    blocks.addAll(buildDoBlockChildren(child));
                } else {
                    Block block = new Block(
                            child,
                            Wrap.createWrap(WrapType.NONE, false),
                            Alignment.createAlignment(),
                            spacingBuilder
                    );

                    blocks.add(block);
                }
            }

            child = child.getTreeNext();
        }

        return blocks;
    }

    private static boolean shouldBuildBlock(@NotNull ASTNode child) {
        return shouldBuildBlock(child, child.getElementType());
    }

    private static boolean shouldBuildBlock(@NotNull ASTNode child, @NotNull IElementType childElementType) {
        return childElementType != TokenType.WHITE_SPACE &&
                !(childElementType == ElixirTypes.END_OF_EXPRESSION &&
                        child.getFirstChildNode().getElementType() == ElixirTypes.EOL);
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
        List<com.intellij.formatting.Block> blocks = new ArrayList<>();

        ASTNode child = doBlock.getFirstChildNode();

        while (child != null) {
            IElementType childElementType = child.getElementType();

            if (shouldBuildBlock(child, childElementType)) {
                if (childElementType == ElixirTypes.STAB) {
                    blocks.addAll(buildStabChildren(child));
                } else {
                    Alignment childAlignment;

                    if (childElementType == ElixirTypes.END) {
                        childAlignment = myAlignment;
                    } else {
                        childAlignment = Alignment.createAlignment();
                    }

                    Block block = new Block(
                            child,
                            Wrap.createWrap(WrapType.NONE, false),
                            childAlignment,
                            spacingBuilder
                    );

                    blocks.add(block);
                }
            }

            child = child.getTreeNext();
        }

        return blocks;
    }

    /**
     * Builds stab.*
     *
     * @param stab a child of a `doBlock`
     * @return children of stab that should be aligned together and indented normal relative to the call with the
     *   doBlock.
     */
    @NotNull
    private List<com.intellij.formatting.Block> buildStabChildren(@NotNull ASTNode stab) {
        List<com.intellij.formatting.Block> blocks = new ArrayList<>();
        ASTNode child = stab.getFirstChildNode();
        Alignment childAlignment = null;
        Indent childIndent = null;

        while (child != null) {
            if (shouldBuildBlock(child)) {
                if (childAlignment == null) {
                    /* all children share the same alignment as expressions inside a doBlock above the stab are assumed
                       to be aligned on the left-side */
                    childAlignment = Alignment.createAlignment();
                }

                if (childIndent == null) {
                    childIndent = Indent.getNormalIndent(true);
                }

                Block block = new Block(
                        child,
                        Wrap.createWrap(WrapType.NONE, false),
                        childAlignment,
                        spacingBuilder,
                        childIndent
                );

                blocks.add(block);
            }

            child = child.getTreeNext();
        }

        return blocks;
    }

    private static final Map<IElementType, Boolean> isUnmatchedCallByElementType = new IdentityHashMap<>();

    static {
        isUnmatchedCallByElementType.put(ElixirTypes.UNMATCHED_AT_UNQUALIFIED_NO_PARENTHESES_CALL, true);
        isUnmatchedCallByElementType.put(ElixirTypes.UNMATCHED_DOT_CALL, true);
        isUnmatchedCallByElementType.put(ElixirTypes.UNMATCHED_QUALIFIED_NO_ARGUMENTS_CALL, true);
        isUnmatchedCallByElementType.put(ElixirTypes.UNMATCHED_QUALIFIED_NO_PARENTHESES_CALL, true);
        isUnmatchedCallByElementType.put(ElixirTypes.UNMATCHED_QUALIFIED_PARENTHESES_CALL, true);
        isUnmatchedCallByElementType.put(ElixirTypes.UNMATCHED_UNQUALIFIED_NO_ARGUMENTS_CALL, true);
        isUnmatchedCallByElementType.put(ElixirTypes.UNMATCHED_UNQUALIFIED_NO_PARENTHESES_CALL, true);
        isUnmatchedCallByElementType.put(ElixirTypes.UNMATCHED_UNQUALIFIED_PARENTHESES_CALL, true);
    }

    private static boolean isUnmatchedCallElementType(IElementType elementType) {
        return isUnmatchedCallByElementType.containsKey(elementType);
    }
}
