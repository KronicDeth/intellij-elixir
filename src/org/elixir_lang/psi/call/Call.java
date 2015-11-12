package org.elixir_lang.psi.call;

import com.intellij.lang.ASTNode;
import org.elixir_lang.psi.ElixirDoBlock;
import org.jetbrains.annotations.Nullable;

/**
 * A general function or macro call.
 */
public interface Call {
    /**
     *
     * @return name of the function/macro as given in the source
     */
    @Nullable
    String functionName();

    /**
     *
     * @return
     */
    @Nullable
    ASTNode functionNameNode();

    /**
     * @return `null` if no `do` block.
     */
    @Nullable
    ElixirDoBlock getDoBlock();

    /**
     *
     * @return name of the qualifying module as given in the source
     */
    @Nullable
    String moduleName();

    /**
     * @return name of the function/macro after taking into account any imports
     */
    @Nullable
    String resolvedFunctionName();

    /**
     * @return name of the qualifying module after taking into account any aliases
     */
    @Nullable
    String resolvedModuleName();
}
