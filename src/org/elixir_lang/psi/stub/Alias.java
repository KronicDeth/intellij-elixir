package org.elixir_lang.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.elixir_lang.psi.ElixirAlias;
import org.jetbrains.annotations.Nullable;

public class Alias extends NamedStubBase<ElixirAlias> {
    public Alias(StubElement parent, IStubElementType elementType, @Nullable String fullyQualifiedName) {
        super(parent, elementType, fullyQualifiedName);
    }

    public Alias(StubElement parent, IStubElementType elementType, StringRef fullyQualifiedNameRef) {
        super(parent, elementType, fullyQualifiedNameRef);
    }
}
