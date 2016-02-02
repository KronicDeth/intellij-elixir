package org.elixir_lang.structure_view.element;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.ElixirAccessExpression;
import org.elixir_lang.psi.ElixirNoParenthesesKeywords;
import org.elixir_lang.psi.QualifiableAlias;
import org.elixir_lang.psi.QuotableKeywordList;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Implementation extends Module {
    /*
     * Static Methods
     */

    public static boolean is(Call call) {
        return call.isCallingMacro("Elixir.Kernel", "defimpl", 2) || call.isCallingMacro("Elixir.Kernel", "defimpl", 3);
    }

    /*
     * Constructors
     */

    public Implementation(@NotNull Call call) {
        this(null, call);
    }

    public Implementation(@Nullable Module parent, @NotNull Call call) {
        super(parent, call);
    }

    /*
     * Instance Methods
     */

    /**
     * The name of the module the protocol is for.
     *
     * @return the {@link #parent} fully-qualified name if no `:for` keyword argument is given; otherwise, the
     *   `:for` keyword argument.
     */
    @NotNull
    public String forName() {
        String forName;
        PsiElement[] finalArguments = ElixirPsiImplUtil.finalArguments(navigationItem);

        assert finalArguments != null;

        if (finalArguments.length > 1) {
            PsiElement finalArgument = finalArguments[finalArguments.length - 1];

            if (finalArgument instanceof QuotableKeywordList) {
                QuotableKeywordList quotableKeywordList = (QuotableKeywordList) finalArgument;
                PsiElement keywordValue = ElixirPsiImplUtil.keywordValue(quotableKeywordList, "for");

                forName = keywordValue.getText();
            } else {
                forName = "?";
            }
        } else if (parent != null) {
            org.elixir_lang.navigation.item_presentation.Parent parentPresentation = (org.elixir_lang.navigation.item_presentation.Parent) parent.getPresentation();
            forName = parentPresentation.getLocatedPresentableText();
        } else {
            forName = "?";
        }

        return forName;
    }

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new org.elixir_lang.navigation.item_presentation.Implementation(
                protocolName(),
                forName()
        );
    }

    /**
     *
     */
    @NotNull
    public String protocolName() {
        PsiElement[] finalArguments = ElixirPsiImplUtil.finalArguments(navigationItem);

        assert finalArguments != null;
        assert finalArguments.length > 0;

        PsiElement firstFinalArgument = finalArguments[0];
        String protocolName = null;

        if (firstFinalArgument instanceof ElixirAccessExpression) {
            ElixirAccessExpression accessExpression = (ElixirAccessExpression) firstFinalArgument;
            PsiElement[] accessExpressionChildren = accessExpression.getChildren();

            if (accessExpressionChildren.length == 1) {
                PsiElement accessExpressionChild = accessExpressionChildren[0];

                if (accessExpressionChild instanceof QualifiableAlias) {
                    QualifiableAlias qualifiableAlias = (QualifiableAlias) accessExpressionChild;

                    String fullyQualifiedName = qualifiableAlias.fullyQualifiedName();

                    if (fullyQualifiedName != null) {
                        // strip the `Elixir.` because no other presentation shows it
                        protocolName = fullyQualifiedName.replace("Elixir.", "");
                    }
                }
            }
        }

        if (protocolName == null) {
            protocolName = "?";
        }

        return protocolName;
    }
}
