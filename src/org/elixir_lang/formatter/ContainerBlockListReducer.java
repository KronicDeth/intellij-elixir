package org.elixir_lang.formatter;

import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ContainerBlockListReducer {
    @NotNull
    List<com.intellij.formatting.Block> reduce(
            @NotNull ASTNode child,
            @NotNull IElementType childElementType,
            @NotNull Wrap tailWrap,
            // Nullable: buildContainerChildren is called with a null elementIndent for maps,
            // structs and interpolation, and every implementation forwards this straight to a
            // buildChild overload that already accepts a null Indent.
            @Nullable Indent childrenIndent,
            @NotNull List<com.intellij.formatting.Block> blockList
    );
}
