package org.elixir_lang.code_insight.lookup.element_renderer;

import com.google.common.base.Joiner;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.diagnostic.LogMessageEx;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.diagnostic.Attachment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
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

        if (psiElement == null) {
            renderObject(element);
        } else {
            renderPsiElement(psiElement, presentation);
        }
    }

    /*
     * Private Instance Methods
     */

    private void renderCall(@NotNull Call call, @NotNull LookupElementPresentation presentation) {
        if (org.elixir_lang.psi.CallDefinitionClause.is(call)) {
            renderCallDefinitionClause(call, presentation);
        }
    }

    private void renderCallDefinitionClause(@NotNull Call call, @NotNull LookupElementPresentation presentation) {
        org.elixir_lang.structure_view.element.CallDefinitionClause structureView =
                org.elixir_lang.structure_view.element.CallDefinitionClause.Companion.fromCall(call);

        if (structureView != null) {
            renderStructureView(structureView, presentation);
        }
    }

    private void renderItemPresentation(@NotNull ItemPresentation itemPresentation,
                                        @NotNull LookupElementPresentation lookupElementPresentation) {
        lookupElementPresentation.setIcon(itemPresentation.getIcon(true));
        String presentableText = itemPresentation.getPresentableText();

        if (presentableText != null) {
            int nameLength = name.length();
            int presentableTextLength = presentableText.length();

            if (nameLength <= presentableTextLength) {
                lookupElementPresentation.appendTailText(presentableText.substring(nameLength), true);
            }
        }

        String locationString = itemPresentation.getLocationString();

        if (locationString != null) {
            lookupElementPresentation.appendTailText(" (" + locationString + ")", false);
        }
    }

    private void renderObject(@NotNull LookupElement lookupElement) {
        Logger logger = Logger.getInstance(CallDefinitionClause.class);

        Object object = lookupElement.getObject();
        String title  = "CallDefinitionClause render called on LookupElement with null getPsiElement";
        String message = "## name\n" +
                "\n" +
                "```\n" +
                name + "\n" +
                "```\n" +
                "\n" +
                "## getObject()\n"+
                "\n" +
                "### toString()\n" +
                "\n" +
                "```\n" +
                object.toString() + "\n" +
                "```\n" +
                "\n" +
                "### getClass().getName()\n" +
                "\n" +
                "```\n" +
                object.getClass().getName() + "\n" +
                "```\n";

        logger.error(message, new Throwable(title));
    }

    private void renderPsiElement(@NotNull PsiElement psiElement, @NotNull LookupElementPresentation presentation) {
        if (psiElement instanceof Call) {
            renderCall((Call) psiElement, presentation);
        }
    }

    private void renderStructureView(@NotNull org.elixir_lang.structure_view.element.CallDefinitionClause structureView,
                                     @NotNull LookupElementPresentation presentation) {
        renderItemPresentation(structureView.getPresentation(), presentation);
    }
}
