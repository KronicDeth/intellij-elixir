package org.elixir_lang.psi.stub.call;

import com.intellij.psi.stubs.StubOutputStream;

import java.io.IOException;

public interface Writer {
    void write(StubOutputStream stubOutputStream) throws IOException;
}
