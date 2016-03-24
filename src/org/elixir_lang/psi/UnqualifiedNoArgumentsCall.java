package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.call.arguments.None;
import org.elixir_lang.psi.qualification.Unqualified;
import org.jetbrains.annotations.NotNull;

/**
 * IDENTIFIER !KEYWORD_PAIR_COLON doBlock?
 */
public interface UnqualifiedNoArgumentsCall<Stub extends org.elixir_lang.psi.stub.call.Stub>
        extends None, Quotable, StubBased<Stub>, Unqualified {
    @NotNull
    @Override
    PsiElement functionNameElement();
}
