package org.elixir_lang.code_insight.lookup.element_renderer;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.elixir_lang.errorreport.Logger;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;

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
                    int nameLength = name.length();
                    int presentableTextLength = presentableText.length();

                    if (nameLength <= presentableTextLength) {
                        presentation.appendTailText(presentableText.substring(nameLength), true);
                    } else {
                        Logger.error(
                                CallDefinitionClause.class,
                                "name (`" + name + "`) is longer than the presentable test (`" + presentableText + "`)",
                                psiElement
                        );
                    }
                }

                String locationString = structureViewPresentation.getLocationString();

                if (locationString != null) {
                    presentation.appendTailText(" (" + locationString + ")", false);
                }
            }
        }
    }
}
