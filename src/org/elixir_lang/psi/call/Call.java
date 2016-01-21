package org.elixir_lang.psi.call;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.ElixirDoBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A general function or macro call.
 */
public interface Call extends NavigatablePsiElement {


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
    PsiElement functionNameElement();

    /**
     * @return `null` if no `do` block.
     */
    @Nullable
    ElixirDoBlock getDoBlock();

    /**
     * Whether this call is calling the given `resolvedFunctionName` in the `resolvedModuleName` with any arity
     *
     * @param resolvedModuleName the expected {@link #resolvedModuleName()}
     * @param resolvedFunctionName the expected {@link #resolvedFunctionName()}
     * @return {@code true} if this call has non-{@code null} {@link #resolvedModuleName()} that equals
     *   {@code resolvedModuleName} and has non-{@code null} {@link #resolvedFunctionName()} that equals
     *   {@code resolvedFunctionName}; otherwise, {@code false}.
     */
    boolean isCalling(@NotNull final String resolvedModuleName, @NotNull final String resolvedFunctionName);

    /**
     * Whether this call is calling the given `resolvedFunctionName` in the `resolvedModuleName` with the
     * `resolvedArity`
     *
     * @param resolvedModuleName  the expected {@link #resolvedModuleName()}
     * @param resolvedFunctionName the expected {@link #resolvedFunctionName()}
     * @param resolvedFinalArity the expected {@link #resolvedFinalArity()}
     * @return {@code true} if this call has non-{@code null} {@link #resolvedModuleName()} that equals
     *   {@code resolvedModuleName} and has non-{@code null} {@link #resolvedFunctionName()} that equals
     *   {@code resolvedFunctionName} and the {@link #resolvedFinalArity}; otherwise, {@code false}.
     */
    boolean isCalling(@NotNull final String resolvedModuleName,
                      @NotNull final String resolvedFunctionName,
                      final int resolvedFinalArity);

    /**
     * Whether {@code call} is of the named macro.
     *
     * Differs from {@link #isCalling(String, String, int)} because this function ensures there is a {@code do}
     * block.  If the macro can be called without a {@code do} block, then
     * {@link #isCalling(String, String, int)} should be used instead.
     *
     * @param resolvedModuleName the expected {@link #resolvedModuleName()}
     * @param resolvedFunctionName the expected {@link #resolvedFunctionName()}
     * @param resolvedFinalArity the expected {@link #resolvedFinalArity()}
     * @return {@code true} if all arguments match and {@link #getDoBlock()} is not {@code null}; {@code false}.
     */
    boolean isCallingMacro(@NotNull final String resolvedModuleName,
                           @NotNull final  String resolvedFunctionName,
                           final int resolvedFinalArity);

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
     *   no arguments, function call like {@code foo}; * @return {@code PsiElement[]} if the function takes arguments.
     *   Importantly, {@code foo} can be distinguished from {@code foo()} because the former returns {@code null} while
     *   the latter returns {@code new PsiElement[0]}
     */
    @Nullable
    PsiElement[] primaryArguments();

    /**
     * The number of {#link primaryArguments}.
     *
     * @return the number of primary arguments without counting the {@code do block} or any pipelines; {@code null}
     *   if {@link #primaryArguments()} is {@code null}.
     */
    @Nullable
    Integer primaryArity();

    /**
     * The arguments in the second set of parentheses.
     *
     * @return {@code null} if the call cannot or does not have a second set of parentheses; otherwise,
     *   {@code PsiElement[]}
     */
    @Nullable
    PsiElement[] secondaryArguments();

    /**
     * The number of {#link secondaryArguments}.
     *
     * @return the number of secondary arguments without counting the {@code do block} or any pipelines; {@code null} if
     *   {@link #secondaryArguments()} is {@code null};
     */
    @Nullable
    Integer secondaryArity();

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

    /**
     * The total number of primary arguments for the final expanded function call.
     *
     * @return the number of primary arguments plus one for a {@code do} block if present plus one if the right operand
     *   of a pipe; If {@code foo} is the function call, and so it has {@code null} {@link #primaryArity()},
     *   {@code foo do} or {@code |> foo} would have a resolve primary arity of {@code null}.  Otherwise, the resolved
     *   primary arity matches {@link #primaryArity()} and it {@code null}.
     */
    @Nullable
    Integer resolvedPrimaryArity();

    /**
     * The final arity that is non-{@code null}.
     *
     * @return {@link #resolvedSecondaryArity()} if it is not {@code null}; otherwise, {@link #resolvedPrimaryArity()}.
     */
    @Nullable
    Integer resolvedFinalArity();

    /**
     * The total number of primary arguments for the final expanded function call.
     *
     * @return the number of primary arguments plus one for a {@code do} block if present plus one if the right operand
     *   of a pipe; {@code null} only if there is no second set of parentheses.
     */
    @Nullable
    Integer resolvedSecondaryArity();
}
