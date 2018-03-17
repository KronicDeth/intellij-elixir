package org.elixir_lang.structure_view.element.modular;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.usageView.UsageViewTypeLocation;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.elixir_lang.psi.call.name.Function.DEFIMPL;
import static org.elixir_lang.psi.call.name.Function.FOR;
import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.psi.impl.PsiElementImplKt.stripAccessExpression;
import static org.elixir_lang.psi.impl.QuotableKeywordListImplKt.keywordValue;
import static org.elixir_lang.psi.impl.call.CallImplKt.finalArguments;

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
    private static Collection<String> forNameCollection(@NotNull ElixirAccessExpression forNameElement) {
        return forNameCollection(forNameElement.getChildren());
    }

    @Nullable
    private static Collection<String> forNameCollection(@NotNull ElixirList forNameElement) {
        return forNameCollection(forNameElement.getChildren());
    }

    @Nullable
    public static Collection<String> forNameCollection(@NotNull PsiElement forNameElement) {
        Collection<String> forNameCollection;

        if (forNameElement instanceof ElixirAccessExpression) {
            forNameCollection = forNameCollection((ElixirAccessExpression) forNameElement);
        } else if (forNameElement instanceof ElixirList) {
            forNameCollection = forNameCollection((ElixirList) forNameElement);
        } else if (forNameElement instanceof QualifiableAlias) {
            forNameCollection = forNameCollection((QualifiableAlias) forNameElement);
        } else if (forNameElement instanceof PsiNamedElement) {
            forNameCollection = forNameCollection((PsiNamedElement) forNameElement);
        } else {
            forNameCollection = Collections.singletonList(forNameElement.getText());
        }

        return forNameCollection;
    }

    @Nullable
    private static Collection<String> forNameCollection(@NotNull PsiElement[] children) {
        Collection<String> forNameCollection = new ArrayList<String>(children.length);

        for (PsiElement child : children) {
            Collection<String> childForNameCollection = forNameCollection(child);

            if (childForNameCollection != null) {
                forNameCollection.addAll(childForNameCollection);
            }
        }

        return forNameCollection;
    }

    @Nullable
    private static Collection<String> forNameCollection(@NotNull PsiNamedElement forNameElement) {
        String forName = forNameElement.getName();
        Collection<String> forNameCollection = null;

        if (forName != null) {
            forNameCollection = Collections.singletonList(forName);
        }

        return forNameCollection;
    }

    @Nullable
    private static Collection<String> forNameCollection(@NotNull QualifiableAlias forNameElement) {
        Collection<String> forNameCollection = null;
        String forName = forNameElement.getName();

        if (forName != null) {
            forNameCollection = Collections.singletonList(forName);
        }

        return forNameCollection;
    }

    @Nullable
    public static Collection<String> forNameCollection(@Nullable Modular enclosingModular, @NotNull Call call) {
        PsiElement forNameElement = forNameElement(call);
        Collection<String> forNameCollection = null;

        if (forNameElement != null) {
            forNameCollection = forNameCollection(forNameElement);
        } else if (enclosingModular != null) {
            org.elixir_lang.navigation.item_presentation.Parent parentPresentation = (org.elixir_lang.navigation.item_presentation.Parent) enclosingModular.getPresentation();
            forNameCollection = Collections.singletonList(parentPresentation.getLocatedPresentableText());
        }

        return forNameCollection;
    }

    @Nullable
    public static PsiElement forNameElement(@NotNull Call call) {
        PsiElement[] finalArguments = finalArguments(call);
        PsiElement forNameElement = null;

        if (finalArguments != null && finalArguments.length > 0) {
            PsiElement finalArgument = finalArguments[finalArguments.length - 1];

            if (finalArgument instanceof QuotableKeywordList) {
                QuotableKeywordList quotableKeywordList = (QuotableKeywordList) finalArgument;
                forNameElement = keywordValue(quotableKeywordList, FOR);
            }
        }

        return forNameElement;
    }

    /**
     * The name of the {@link #navigationItem}.
     *
     * @return the {@link NamedElement#getName()} if {@link #navigationItem} is a {@link NamedElement}; otherwise,
     *   {@code null}.
     */
    @Nullable
    @Override
    public String getName() {
        String name = null;

        if (forNameOverride != null) {
            String protocolName = protocolName(navigationItem);

            if (protocolName != null) {
                name = protocolName + "." + forNameOverride;
            }
        } else if (navigationItem instanceof NamedElement) {
            NamedElement namedElement = (NamedElement) navigationItem;
            name = namedElement.getName();
        }

        return name;
    }

    public static boolean is(Call call) {
        return call.isCallingMacro(KERNEL, DEFIMPL, 2) ||
                call.isCallingMacro(KERNEL, DEFIMPL, 3);
    }

    /**
     * @return {@code null} if protocol or module for the implementation cannot be derived or if the for argument is a
     *   list.
     */
    @Nullable
    public static String name(@NotNull Call call) {
        Collection<String> nameCollection = nameCollection(CallDefinitionClause.enclosingModular(call), call);
        String name = null;

        if (nameCollection != null && nameCollection.size() == 1) {
            name = nameCollection.iterator().next();
        }

        // TODO Use CachedValueManager
        return name;
    }

    @Nullable
    public static Collection<String> nameCollection(@Nullable Modular enclosingModular, @NotNull Call call) {
        String protocolName = protocolName(call);
        Collection<String> forNameCollection = forNameCollection(enclosingModular, call);
        Collection<String> nameCollection = null;

        if (protocolName != null && forNameCollection != null) {
            nameCollection = new ArrayList<String>();

            for (String forName : forNameCollection) {
                nameCollection.add(protocolName + "." + forName);
            }
        }

        return nameCollection;
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
        PsiElement[] finalArguments = finalArguments(call);
        QualifiableAlias protocolNameElement = null;

        if (finalArguments != null && finalArguments.length > 0) {
            PsiElement firstFinalArgument = finalArguments[0];

            if (firstFinalArgument instanceof ElixirAccessExpression) {
                PsiElement accessExpressionChild = stripAccessExpression(firstFinalArgument);

                if (accessExpressionChild instanceof QualifiableAlias) {
                    protocolNameElement = (QualifiableAlias) accessExpressionChild;
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
     * Fields
     */

    @Nullable
    private final String forNameOverride;

    /*
     * Constructors
     */

    public Implementation(@NotNull Call call) {
        this(null, call);
    }

    public Implementation(@Nullable Modular parent, @NotNull Call call) {
        super(parent, call);
        this.forNameOverride = null;
    }

    /**
     * Implementation that presents as having a single {@code forName} when the {@code defimpl} has a list as the
     * keyword argument of {@code for:}.
     *
     * @param parent enclosing modular
     * @param call the {@code defimpl} call
     * @param forNameOverride The forName to use when the {@code call} has a list for its {@code for:} value.  Needed so
     *                        that rendered named in the presentation uses {@code forName} for
     *                        {@link org.elixir_lang.navigation.GotoSymbolContributor}'s lookup menu
     */
    public Implementation(@Nullable Modular parent, @NotNull Call call, @NotNull String forNameOverride) {
        super(parent, call);
        this.forNameOverride = forNameOverride;
    }

    /*
     * Instance Methods
     */

    /**
     * The name of the module the protocol is for as derived from the PSI tree
     *
     * @return the {@link #parent} fully-qualified name if no `:for` keyword argument is given; otherwise, the
     *   `:for` keyword argument.
     */
    @NotNull
    private String derivedForName() {
        String forName;
        PsiElement[] finalArguments = finalArguments(navigationItem);

        assert finalArguments != null;

        if (finalArguments.length > 1) {
            PsiElement finalArgument = finalArguments[finalArguments.length - 1];

            if (finalArgument instanceof QuotableKeywordList) {
                QuotableKeywordList quotableKeywordList = (QuotableKeywordList) finalArgument;
                PsiElement keywordValue = keywordValue(quotableKeywordList, FOR);

                if (keywordValue != null) {
                    forName = keywordValue.getText();
                } else {
                    forName = "?";
                }
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
     * The name of the module the protocol is for.
     *
     * @return the {@link #forNameOverride}; the {@link #parent} fully-qualified name if no `:for` keyword argument is
     *   given; otherwise, the `:for` keyword argument.
     */
    @NotNull
    private String forName() {
        String forName;

        if (forNameOverride != null) {
            forName = forNameOverride;
        } else {
            forName = derivedForName();
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
