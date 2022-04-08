package org.elixir_lang.beam.psi;

import com.intellij.psi.PsiCompiledElement;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.call.CanonicallyNamed;
import org.elixir_lang.psi.call.MaybeExported;

public interface CallDefinition extends CanonicallyNamed, MaybeExported, NamedElement, PsiCompiledElement {
}
