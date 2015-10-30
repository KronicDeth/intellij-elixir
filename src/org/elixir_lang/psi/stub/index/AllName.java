package org.elixir_lang.psi.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.elixir_lang.psi.NamedElement;
import org.jetbrains.annotations.NotNull;

public class AllName extends StringStubIndexExtension<NamedElement> {
    public static final StubIndexKey<String, NamedElement> KEY = StubIndexKey.createIndexKey("elixir.all.name");
    public static final int VERSION = 0;

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, NamedElement> getKey() {
        return KEY;
    }
}
