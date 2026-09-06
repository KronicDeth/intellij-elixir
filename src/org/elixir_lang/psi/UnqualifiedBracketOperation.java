package org.elixir_lang.psi;

import com.intellij.model.psi.PsiExternalReferenceHost;
import org.elixir_lang.psi.qualification.Unqualified;
import org.jetbrains.annotations.NotNull;

/**
 * IDENTIFIER CALL bracketArguments
 *
 * <p>The identifier is the receiver of the access, `m` in `m[k]`, and reads a variable, so the operation hosts
 * that variable's reference; the parser gives the receiver as a bare identifier rather than a call.
 */
@SuppressWarnings("UnstableApiUsage")
public interface UnqualifiedBracketOperation extends BracketOperation, Unqualified, PsiExternalReferenceHost {
    @NotNull ElixirIdentifier getIdentifier();
}
