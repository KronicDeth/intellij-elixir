package org.elixir_lang.psi.call;

import com.intellij.psi.StubBasedPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public interface StubBased<Stub extends org.elixir_lang.psi.stub.call.Stub> extends Named, StubBasedPsiElement<Stub> {
    /**
     * @return {@code null} if it does not have a canonical name OR if it has more than one canonical name
     */
    @Nullable
    String canonicalName();

    /**
     * @return empty set if no canonical names
     */
    @NotNull
    Set<String> canonicalNameSet();
}
