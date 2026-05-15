package org.elixir_lang.psi.stub.call;

import com.intellij.psi.stubs.StubInputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface Reader<T> {
    T read(@NotNull StubInputStream stubInputStream) throws IOException;
}
