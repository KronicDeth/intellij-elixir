package org.elixir_lang.heex.file.psi;

import com.intellij.lang.Language;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;
import com.intellij.psi.impl.source.html.HtmlFileImpl;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.LightVirtualFile;
import org.elixir_lang.heex.HeexLanguage;
import org.elixir_lang.heex.html.HeexHTMLFileElementType;
import org.elixir_lang.heex.html.HeexHTMLLanguage;
import org.elixir_lang.heex.psi.Types;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TemplateData extends TemplateDataElementType {
    public static final TemplateData INSTANCE = new TemplateData(
      "HEEX_TEMPLATE_DATA",
      HeexLanguage.INSTANCE,
      Types.DATA,
      Types.HEEX_OUTER_ELEMENT
    );

    protected TemplateData(@NonNls String debugName, Language language, @NotNull IElementType templateElementType, @NotNull IElementType outerElementType) {
        super(debugName, language, templateElementType, outerElementType);
    }

    @Override
    protected boolean isInsertionToken(@Nullable IElementType tokenType, @NotNull CharSequence tokenSequence) {
        return true;
    }

    @Override
    protected PsiFile createPsiFileFromSource(final Language language, CharSequence sourceCode, PsiManager manager) {
        if (language == HTMLLanguage.INSTANCE) {
            return createSpoofedPsiFileForHTML(sourceCode, manager);
        }

        return super.createPsiFileFromSource(language, sourceCode, manager);
    }

    /** For HTML, we manually create the PSI file so we can force it to use our custom lexer */
    private PsiFile createSpoofedPsiFileForHTML(CharSequence sourceCode, PsiManager manager) {
        LightVirtualFile virtualFile = new LightVirtualFile("HEExHTML", this.createTemplateFakeFileType(HeexHTMLLanguage.INSTANCE), sourceCode);
        FileViewProvider viewProvider = new SingleRootFileViewProvider(manager, virtualFile, false) {
            public @NotNull Language getBaseLanguage() {
                return HTMLLanguage.INSTANCE;
            }
        };

        return new HtmlFileImpl(viewProvider, HeexHTMLFileElementType.INSTANCE);
    }
}
