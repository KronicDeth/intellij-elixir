package org.elixir_lang.formatter;

import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ContainerBlockListReducer {
    @NotNull
    List<com.intellij.formatting.Block> reduce(
            @NotNull ASTNode child,
            @NotNull IElementType childElementType,
            @NotNull Wrap tailWrap,
            @NotNull Indent childrenIndent,
            @NotNull List<com.intellij.formatting.Block> blockList
    );
}
