package org.elixir_lang.psi.call;

import com.intellij.psi.StubBasedPsiElement;

public interface StubBased<Stub extends org.elixir_lang.psi.stub.call.Stub> extends Named, StubBasedPsiElement<Stub> {
}
