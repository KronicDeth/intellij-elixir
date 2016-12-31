package org.elixir_lang.beam.psi;

import com.intellij.psi.PsiCompiledElement;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.call.CanonicallyNamed;

public interface Module extends CanonicallyNamed, NamedElement, PsiCompiledElement {
}
