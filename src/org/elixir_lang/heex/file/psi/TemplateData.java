package org.elixir_lang.heex.file.psi;

import com.intellij.lang.Language;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.heex.HeexLanguage;
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
}
