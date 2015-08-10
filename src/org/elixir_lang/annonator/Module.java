package org.elixir_lang.annonator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.elixir_lang.Util;
import org.intellij.erlang.psi.ErlangAtom;
import org.jetbrains.annotations.NotNull;

public class Module implements Annotator {

    public static final String ELIXIR_ALIAS_PREFIX = "Elixir.";

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull com.intellij.lang.annotation.AnnotationHolder annotationHolder) {
        if (psiElement instanceof ErlangAtom) {
            ErlangAtom erlangAtom = (ErlangAtom) psiElement;
            String name = erlangAtom.getName();

            if (name.startsWith(ELIXIR_ALIAS_PREFIX)) {
                Project project = psiElement.getProject();

                if (Util.findModuleDefinition(project, name) != null) {
                    TextRange textRange = psiElement.getTextRange();
                    String unprefixedName = name.substring(ELIXIR_ALIAS_PREFIX.length(), name.length());
                    Annotation annotation = annotationHolder.createInfoAnnotation(textRange, "Resolves to Elixir Module " + unprefixedName);
                    annotation.setTextAttributes(DefaultLanguageHighlighterColors.LINE_COMMENT);
                } else {
                    TextRange textRange = psiElement.getTextRange();
                    annotationHolder.createErrorAnnotation(textRange, "Unresolved Elixir Module");
                }
            }
        }
    }
}
