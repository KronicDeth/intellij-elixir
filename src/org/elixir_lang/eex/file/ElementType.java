package org.elixir_lang.eex.file;

import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import org.elixir_lang.eex.File;
import org.elixir_lang.eex.Language;
import org.elixir_lang.eex.file.psi.Stub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ElementType extends IStubFileElementType<Stub> {
    public static final int VERSION = 1;
    public static final IStubFileElementType INSTANCE = new ElementType();

    public ElementType() {
        super("EEX_FILE", Language.INSTANCE);
    }

    @Override
    public StubBuilder getBuilder() {
        return new DefaultStubBuilder() {
            @Override
            protected StubElement createStubForFile(@NotNull PsiFile psiFile) {
                StubElement stubElement;

                if (psiFile instanceof File) {
                    stubElement = new Stub((File) psiFile);
                } else {
                    stubElement = super.createStubForFile(psiFile);
                }

                return stubElement;
            }
        };
    }

    @Override
    public int getStubVersion() {
        return VERSION;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "elixir.embedded.FILE";
    }

    @Override
    public void serialize(@NotNull Stub stub, @NotNull StubOutputStream dataStream) throws IOException {
    }

    @NotNull
    @Override
    public Stub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new Stub(null);
    }

}
