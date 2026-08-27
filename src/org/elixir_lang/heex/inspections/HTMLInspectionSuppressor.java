package org.elixir_lang.heex.inspections;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.codeInspection.htmlInspections.XmlEntitiesInspection;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import org.elixir_lang.heex.HeexPsiKt;
import org.elixir_lang.heex.xml.ComponentTagName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HTMLInspectionSuppressor implements InspectionSuppressor {
    // Both are InspectionProfileEntry#getSuppressId(), which defaults to getShortName() - reading
    // it off a throwaway instance isn't needed, these are compile-time constants.
    public static final List<String> SUPPRESSED_INSPECTIONS = List.of(
        // com.intellij.xml.util.CheckEmptyTagInspection#getShortName() - no exported constant.
        "CheckEmptyScriptTag",
        XmlEntitiesInspection.TAG_SHORT_NAME
    );


    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
        if (!SUPPRESSED_INSPECTIONS.contains(toolId)) {
            return false;
        }

        if (HeexPsiKt.isInHeex(element)) {
            XmlTag xmlTag = PsiTreeUtil.getParentOfType(element, XmlTag.class, false);

            // Only valid HEEx component or slot syntax is suppressed; a dotted or colon-prefixed
            // name that Phoenix.LiveView.HTMLEngine.classify_type/1 would reject is flagged like any typo.
            return xmlTag != null && ComponentTagName.parse(xmlTag.getName()) != null;
        }

        return false;
    }

    @Override
    public SuppressQuickFix @NotNull [] getSuppressActions(@Nullable PsiElement psiElement, @NotNull String s) {
        return SuppressQuickFix.EMPTY_ARRAY;
    }
}
