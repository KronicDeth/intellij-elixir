package org.elixir_lang.heex.html;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.HtmlAutoPopupHandler;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlText;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.annotations.NotNull;

public class HeexHTMLAutoPopupHandler extends HtmlAutoPopupHandler {
    @NotNull
    public TypedHandlerDelegate.Result checkAutoPopup(char charTyped, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (charTyped == '&' && file instanceof HeexHTMLFileImpl) {
            PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
            if (element == null) {
                return Result.CONTINUE;
            } else {
                IElementType elementType = element.getNode().getElementType();
                PsiElement parent = element.getParent();
                if (elementType == XmlTokenType.XML_END_TAG_START || elementType == XmlTokenType.XML_ATTRIBUTE_VALUE_END_DELIMITER || elementType == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN || (elementType == XmlTokenType.XML_DATA_CHARACTERS || elementType == XmlTokenType.XML_WHITE_SPACE) && parent instanceof XmlText) {
                    AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
                    return Result.STOP;
                } else {
                    return Result.CONTINUE;
                }
            }
        } else {
            return Result.CONTINUE;
        }
    }
}
