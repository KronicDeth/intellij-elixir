package org.elixir_lang.psi.stub.type;

import com.intellij.lang.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.psi.ElixirFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class File extends IStubFileElementType<org.elixir_lang.psi.stub.File> {
    public static final int VERSION = 3;
    public static final IStubFileElementType INSTANCE = new File();

    public File() {
        super("ELIXIR_FILE", ElixirLanguage.INSTANCE);
    }

    @Override
    public StubBuilder getBuilder() {
        return new DefaultStubBuilder() {
            @Override
            protected StubElement createStubForFile(@NotNull PsiFile file) {
                if (file instanceof ElixirFile) {
                    return new org.elixir_lang.psi.stub.File((ElixirFile) file);
                }
                return super.createStubForFile(file);
            }
        };
    }

    @Override
    public int getStubVersion() {
        return VERSION;
    }

    @Override
    public void serialize(@NotNull org.elixir_lang.psi.stub.File stub, @NotNull StubOutputStream dataStream) throws IOException {
    }

    @NotNull
    @Override
    public org.elixir_lang.psi.stub.File deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new org.elixir_lang.psi.stub.File(null);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "elixir.FILE";
    }

    @Nullable
    @Override
    protected ASTNode doParseContents(@NotNull ASTNode chameleon, @NotNull PsiElement psi) {
        Project project = psi.getProject();
        Language languageForParser = getLanguageForParser(psi);
        PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, languageForParser, chameleon.getChars());
        PsiParser parser = LanguageParserDefinitions.INSTANCE.forLanguage(languageForParser).createParser(project);
        ASTNode node = parser.parse(this, builder);
        return node.getFirstChildNode();
    }
}
