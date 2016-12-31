package org.elixir_lang.psi.stub.call;

import com.intellij.psi.PsiReference;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface Stubbic {
    /**
     * These names do not depend on aliases or nested modules.
     *
     * @return the canonical texts of the reference
     * @see PsiReference#getCanonicalText()
     */
    Set<String> canonicalNameSet();

    /**
     * Whether this call has a {@code do} block or a {@code :do} keyword, so it is a macro
     *
     * @return {@code true} if {@link Call#getDoBlock()} is NOT {@code null} or there is a {@code "do"} keyword argument
     * @see org.elixir_lang.psi.impl.ElixirPsiImplUtil#keywordArgument(Call, String)
     */
    boolean hasDoBlockOrKeyword();

    /**
     * The final arity that is non-{@code null}.
     *
     * @return {@link Call#resolvedSecondaryArity()} if it is not {@code null}; {@link Call#resolvedPrimaryArity()} if
     *   it is not {@code null}; otherwise, {@code 0}.
     */
    Integer resolvedFinalArity();

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

    @Nullable
    String getName();
}
