package org.elixir_lang.psi.stub;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import org.elixir_lang.psi.ElixirFile;

public class File extends PsiFileStubImpl<ElixirFile> {
    public File(ElixirFile file) {
        super(file);
    }

    @Override
    public IStubFileElementType getType() {
        return org.elixir_lang.psi.stub.type.File.INSTANCE;
    }
}
