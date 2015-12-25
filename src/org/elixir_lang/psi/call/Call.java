package org.elixir_lang.psi.call;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.ElixirDoBlock;
import org.jetbrains.annotations.Nullable;

/**
 * A general function or macro call.
 */
public interface Call extends PsiElement {
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
     * The arguments directly after the {@link #functionName}.  If the function cannot have arguments, then this will `null`
     *
     * @return {@code null} if function cannot take arguments, such as an ambiguous variable or no parentheses,
     *         no arguments, function call like {@code foo}.
     * @return {@code PsiElement[]} if the function takes arguments.  Importantly, {@code foo} can be distinguished from
     *         {@code foo()} because the formmer returns {@code null} while the latter returns {@code new PsiElement[0]}
     */
    @Nullable
    PsiElement[] primaryArguments();

    /**
     * The arguments in the second set of parentheses.
     *
     * @return {@code null} if the call cannot or does not have a second set of parentheses.
     * @return {@code PsiElement[]}
     */
    @Nullable
    PsiElement[] secondaryArguments();

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
