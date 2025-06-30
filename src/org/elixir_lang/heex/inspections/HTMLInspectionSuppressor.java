package org.elixir_lang.heex.inspections;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.codeInspection.htmlInspections.HtmlUnknownTagInspection;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.util.CheckEmptyTagInspection;
import org.elixir_lang.heex.html.HeexHTMLLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HTMLInspectionSuppressor implements InspectionSuppressor {
    public static final List<String> SUPPRESSED_INSPECTIONS = List.of(
        new CheckEmptyTagInspection().getSuppressId(),
        new HtmlUnknownTagInspection().getSuppressId()
    );


    public boolean isSuppressedFor(PsiElement element, String toolId) {
        if (!SUPPRESSED_INSPECTIONS.contains(toolId)) {
            return false;
        }

        PsiFile file = element.getContainingFile();
        if (file != null && file.getViewProvider().hasLanguage(HeexHTMLLanguage.INSTANCE)) {
            XmlTag xmlTag = PsiTreeUtil.getParentOfType(element, XmlTag.class, false);

            // Tag names that contain dots are HEEx components
            return xmlTag != null && xmlTag.getName().contains(".");
        }

        return false;
    }

    @Override
    public SuppressQuickFix @NotNull [] getSuppressActions(@Nullable PsiElement psiElement, @NotNull String s) {
        return SuppressQuickFix.EMPTY_ARRAY;
    }
}
