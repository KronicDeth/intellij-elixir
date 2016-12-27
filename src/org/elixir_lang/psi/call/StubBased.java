package org.elixir_lang.psi.call;

import com.intellij.psi.StubBasedPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public interface StubBased<Stub extends org.elixir_lang.psi.stub.call.Stub>
        extends CanonicallyNamed, Named, StubBasedPsiElement<Stub> {
}
