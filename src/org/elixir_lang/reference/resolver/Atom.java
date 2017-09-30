package org.elixir_lang.reference.resolver;

import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.elixir_lang.psi.ElixirAtom;
import org.elixir_lang.reference.resolver.atom.Resolvable;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.reference.resolver.atom.Resolvable.resolvable;

public class Atom implements ResolveCache.PolyVariantResolver<org.elixir_lang.reference.Atom>{
    public static final Atom INSTANCE = new Atom();

    @NotNull
    @Override
    public ResolveResult[] resolve(@NotNull org.elixir_lang.reference.Atom atom, boolean incompleteCode) {
        ElixirAtom element = atom.getElement();
        Resolvable resolvable = resolvable(element);

        return resolvable.resolve(element.getProject());
    }
}
