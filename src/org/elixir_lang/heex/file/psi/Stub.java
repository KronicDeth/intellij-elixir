package org.elixir_lang.heex.file.psi;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import org.elixir_lang.heex.file.ElementType;
import org.elixir_lang.heex.File;
import org.jetbrains.annotations.NotNull;

public class Stub extends PsiFileStubImpl<File> {
    public Stub(File file) {
        super(file);
    }

    @NotNull
    @Override
    public IStubFileElementType getType() {
        return ElementType.INSTANCE;
    }
}
