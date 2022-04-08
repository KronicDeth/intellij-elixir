package org.elixir_lang.psi.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.elixir_lang.psi.NamedElement;
import org.jetbrains.annotations.NotNull;

public class AllName extends StringStubIndexExtension<NamedElement> {
    public static final StubIndexKey<String, NamedElement> KEY = StubIndexKey.createIndexKey("elixir.all.name");
    // 4 - adds defp and defmacrop to decompiled beam files
    /* 5 - fix bug in Module.is (https://github.com/KronicDeth/intellij-elixir/issues/1301) that caused defmodule macro
           redefinition to count as actual module */
    // 6 - Remove non-canonical names from being index
    // 7 - `defmemo` and `defmemop`
    // 8 - `implementedProtocolName` added to `Deserialized`
    // 9 - `Definition.MODULE_ATTRIBUTE` added
    // 10 - `Definition.VARIABLE` added
    // 11 - `ModuleDefinitonStub`, `CallDefinitionStub` and `TypeDefinitonStub` simplified
    public static final int VERSION = 11;

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
