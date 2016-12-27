package org.elixir_lang.beam.psi.stubs;

import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.PsiFileStub;
import org.elixir_lang.psi.call.CanonicallyNamed;

public interface ModuleHolderFileStub<T extends PsiFile> extends PsiFileStub<T> {
    CanonicallyNamed[] modulars();
}
