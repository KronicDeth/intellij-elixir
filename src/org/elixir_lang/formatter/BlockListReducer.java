package org.elixir_lang.formatter;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BlockListReducer {
    @NotNull
    List<com.intellij.formatting.Block> reduce(@NotNull ASTNode child,
                                               @NotNull IElementType childElementType,
                                               @NotNull List<com.intellij.formatting.Block> blockList);
}
