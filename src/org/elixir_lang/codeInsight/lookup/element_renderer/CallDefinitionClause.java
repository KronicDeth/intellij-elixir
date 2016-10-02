package org.elixir_lang.codeInsight.lookup.element_renderer;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import org.elixir_lang.ElixirSyntaxHighlighter;
import org.elixir_lang.annonator.Parameter;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.operation.InMatch;
import org.elixir_lang.psi.operation.Match;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static org.elixir_lang.reference.Callable.*;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.head;

public class CallDefinitionClause extends com.intellij.codeInsight.lookup.LookupElementRenderer<LookupElement> {
    /*
     * Fields
     */

    @NotNull
    private final String name;

    /*
     * Constructors
     */

    public CallDefinitionClause(@NotNull String name) {
        this.name = name;
    }

    /*
     * Public Instance Methods
     */

    @Override
    public void renderElement(LookupElement element, LookupElementPresentation presentation) {
        presentation.setItemText(name);
        presentation.setItemTextBold(true);

        PsiElement psiElement = element.getPsiElement();

        assert psiElement != null;

        if (psiElement instanceof Call) {
            Call call = (Call) psiElement;

            if (org.elixir_lang.structure_view.element.CallDefinitionClause.is(call)) {
                org.elixir_lang.structure_view.element.CallDefinitionClause structureView =
                        new org.elixir_lang.structure_view.element.CallDefinitionClause(call);
                ItemPresentation structureViewPresentation = structureView.getPresentation();

                presentation.setIcon(structureViewPresentation.getIcon(true));
                String presentableText = structureViewPresentation.getPresentableText();

                if (presentableText != null) {
                    presentation.appendTailText(presentableText.substring(name.length()), true);
                }

                String locationString = structureViewPresentation.getLocationString();

                if (locationString != null) {
                    presentation.appendTailText(" (" + locationString + ")", false);
                }
            }
        }
    }
}
