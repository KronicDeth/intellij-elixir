package org.elixir_lang.structure_view.element.modular;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewTypeLocation;
import org.elixir_lang.psi.ElixirAccessExpression;
import org.elixir_lang.psi.QualifiableAlias;
import org.elixir_lang.psi.QuotableKeywordList;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.call.name.Function.DEFIMPL;
import static org.elixir_lang.psi.call.name.Function.FOR;
import static org.elixir_lang.psi.call.name.Module.KERNEL;

public class Implementation extends Module {
    /*
     *
     * Static Methods
     *
     */

    /*
     * Public Static Methods
     */

    public static String elementDescription(Call call, ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "implementation";
        }

        return elementDescription;
    }

    @Nullable
    public static String forName(@Nullable Modular enclosingModular, @NotNull Call call) {
        PsiElement[] finalArguments = ElixirPsiImplUtil.finalArguments(call);
        String forName = null;

        if (finalArguments != null) {
            if (finalArguments.length > 1) {
                PsiElement finalArgument = finalArguments[finalArguments.length - 1];

                if (finalArgument instanceof QuotableKeywordList) {
                    QuotableKeywordList quotableKeywordList = (QuotableKeywordList) finalArgument;
                    PsiElement keywordValue = ElixirPsiImplUtil.keywordValue(quotableKeywordList, FOR);

                    forName = keywordValue.getText();
                }
            } else if (enclosingModular != null) {
                org.elixir_lang.navigation.item_presentation.Parent parentPresentation = (org.elixir_lang.navigation.item_presentation.Parent) enclosingModular.getPresentation();
                forName = parentPresentation.getLocatedPresentableText();
            }
        }

        return forName;
    }

    public static boolean is(Call call) {
        return call.isCallingMacro(KERNEL, DEFIMPL, 2) ||
                call.isCallingMacro(KERNEL, DEFIMPL, 3);
    }

    public static String name(@NotNull Call call) {
        // TODO Use CachedValueManager
        return name(CallDefinitionClause.enclosingModular(call), call);
    }


    @Nullable
    public static String name(@Nullable Modular enclosingModular, @NotNull Call call) {
        String protocolName = protocolName(call);
        String forName = forName(enclosingModular, call);
        String name = null;

        if (protocolName != null && forName != null) {
          name = protocolName + "." + forName;
        }

        return name;
    }

    @Nullable
    public static String protocolName(@NotNull Call call) {
        QualifiableAlias protocolNameElement = protocolNameElement(call);
        String protocolName = null;

        if (protocolNameElement != null) {
            protocolName = protocolName(protocolNameElement);
        }

        return protocolName;
    }

    @Nullable
    public static QualifiableAlias protocolNameElement(@NotNull Call call) {
        PsiElement[] finalArguments = ElixirPsiImplUtil.finalArguments(call);
        QualifiableAlias protocolNameElement = null;

        if (finalArguments != null && finalArguments.length > 0) {
            PsiElement firstFinalArgument = finalArguments[0];

            if (firstFinalArgument instanceof ElixirAccessExpression) {
                ElixirAccessExpression accessExpression = (ElixirAccessExpression) firstFinalArgument;
                PsiElement[] accessExpressionChildren = accessExpression.getChildren();

                if (accessExpressionChildren.length == 1) {
                    PsiElement accessExpressionChild = accessExpressionChildren[0];

                    if (accessExpressionChild instanceof QualifiableAlias) {
                        protocolNameElement = (QualifiableAlias) accessExpressionChild;
                    }
                }
            } else if (firstFinalArgument instanceof QualifiableAlias) {
                protocolNameElement = (QualifiableAlias) firstFinalArgument;
            }
        }

        return protocolNameElement;
    }

    /*
     * Private Static Methods
     */

    @Nullable
    private static String protocolName(QualifiableAlias qualifiableAlias) {
        String fullyQualifiedName = qualifiableAlias.fullyQualifiedName();
        String protocolName = null;

        if (fullyQualifiedName != null) {
            // strip the `Elixir.` because no other presentation shows it
            protocolName = fullyQualifiedName.replace("Elixir.", "");
        }

        return protocolName;
    }

    /*
     * Constructors
     */

    public Implementation(@NotNull Call call) {
        this(null, call);
    }

    public Implementation(@Nullable Modular parent, @NotNull Call call) {
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
                PsiElement keywordValue = ElixirPsiImplUtil.keywordValue(quotableKeywordList, FOR);

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
     * Unlike {@link #protocolName(Call)}, will return "?" when the protocol name can't be derived from the call.
     */
    @NotNull
    public String protocolName() {
        String protocolName = protocolName(navigationItem);

        if (protocolName == null) {
            protocolName = "?";
        }

        return protocolName;
    }
}
