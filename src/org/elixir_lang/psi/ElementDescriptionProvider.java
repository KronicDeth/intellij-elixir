package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import org.elixir_lang.annonator.Parameter;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.reference.Callable;
import org.elixir_lang.structure_view.element.*;
import org.elixir_lang.structure_view.element.modular.Implementation;
import org.elixir_lang.structure_view.element.modular.Module;
import org.elixir_lang.structure_view.element.modular.Protocol;
import org.elixir_lang.structure_view.element.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.call.name.Function.ALIAS;
import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.hasKeywordKey;
import static org.elixir_lang.reference.module.ResolvableName.resolvableName;

/**
 * Dual to {@link org.elixir_lang.FindUsagesProvider}, where instead of each location being a separate method, they
 * are all one method, which means the same code can be used to detect the type of an element and then group all the
 * text ({@link org.elixir_lang.FindUsagesProvider#getDescriptiveName(PsiElement)},
 * {@link org.elixir_lang.FindUsagesProvider#getHelpId(PsiElement)},
 * {@link org.elixir_lang.FindUsagesProvider#getNodeText(PsiElement, boolean)}
 * {@link org.elixir_lang.FindUsagesProvider#getType(PsiElement)}) together together.
 */
public class ElementDescriptionProvider implements com.intellij.psi.ElementDescriptionProvider {
    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    @Nullable
    @Override
    public String getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
        String elementDescription = null;

        if (element instanceof Call) {
            elementDescription = getElementDescription((Call) element, location);
        } else if (element instanceof ElixirIdentifier) {
            elementDescription = getElementDescription((ElixirIdentifier) element, location);
        } else if (element instanceof ElixirKeywordKey) {
            elementDescription = getElementDescription((ElixirKeywordKey) element, location);
        } else if (element instanceof MaybeModuleName) {
            elementDescription = getElementDescription((MaybeModuleName) element, location);
        }

        return elementDescription;
    }

    /*
     * Private Instance Methods
     */

    @Nullable
    private String getElementDescription(@NotNull ElixirIdentifier identifier, @NotNull ElementDescriptionLocation location) {
        String elementDescription = null;

        Parameter parameter = new Parameter(identifier);
        Parameter.Type type = Parameter.putParameterized(parameter).type;

        if (type == Parameter.Type.FUNCTION_NAME) {
            if (location == UsageViewShortNameLocation.INSTANCE) {
                elementDescription = identifier.getText();
            } else if (location == UsageViewTypeLocation.INSTANCE) {
                elementDescription = "function";
            }
        } else if (type == Parameter.Type.MACRO_NAME) {
            if (location == UsageViewShortNameLocation.INSTANCE) {
                elementDescription = identifier.getText();
            } else if (location == UsageViewTypeLocation.INSTANCE) {
                elementDescription = "macro";
            }
        }

        return elementDescription;
    }

    @Nullable
    private String getElementDescription(@NotNull ElixirKeywordKey keywordKey,
                                         @NotNull ElementDescriptionLocation location) {
        String elementDescription = null;

        PsiElement innerKeywordPair = keywordKey.getParent();

        if (innerKeywordPair instanceof ElixirKeywordPair) {
            PsiElement innerKeywords = innerKeywordPair.getParent();

            if (innerKeywords instanceof ElixirKeywords) {
                PsiElement innerList = innerKeywords.getParent();

                if (innerList instanceof ElixirList) {
                    PsiElement outerAccessExpression = innerList.getParent();

                    if (outerAccessExpression instanceof ElixirAccessExpression) {
                        PsiElement outerKeywordPair = outerAccessExpression.getParent();

                        if (outerKeywordPair instanceof QuotableKeywordPair) {
                            QuotableKeywordPair outerQuotableKeywordPair = (QuotableKeywordPair) outerKeywordPair;
                            Quotable outerKeywordKey = outerQuotableKeywordPair.getKeywordKey();

                            if (outerKeywordKey.getText().equals("bind_quoted")) {
                                if (location == UsageViewTypeLocation.INSTANCE) {
                                    elementDescription = "quote bound variable";
                                }
                            }
                        }
                    }
                }
            }
        }

        if (elementDescription == null) {
            if (location == UsageViewTypeLocation.INSTANCE) {
                elementDescription = "keyword key";
            }
        }

        return elementDescription;
    }

    @Nullable
    private String getElementDescription(@NotNull MaybeModuleName maybeModuleName,
                                         @NotNull ElementDescriptionLocation location) {
        String elementDescription = null;

        if (maybeModuleName instanceof QualifiableAlias) {
            QualifiableAlias qualifiableAlias = (QualifiableAlias) maybeModuleName;

            if (location == UsageViewShortNameLocation.INSTANCE) {
                if (isAliasCallAs(qualifiableAlias)) {
                    String resolvableName = resolvableName(qualifiableAlias);

                    if (resolvableName != null) {
                        elementDescription = "as: " + resolvableName;
                    }
                } else {
                    elementDescription = resolvableName(qualifiableAlias);
                }
            }
        }

        if (location == UsageViewTypeLocation.INSTANCE) {
            if (isAliasCallArgument(maybeModuleName)) {
                elementDescription = "alias";
            } else {
                elementDescription = "module";
            }

        }

        return elementDescription;
    }

    @Nullable
    private String getElementDescription(@NotNull Call call, @NotNull ElementDescriptionLocation location) {
        String elementDescription = null;

        if (CallDefinitionClause.is(call)) {
            elementDescription = CallDefinitionClause.elementDescription(call, location);
        } else if (CallDefinitionSpecification.is(call)) {
            elementDescription = CallDefinitionSpecification.elementDescription(call, location);
        } else if (Callback.is(call)) {
            elementDescription = Callback.elementDescription(call, location);
        } else if (Delegation.is(call)) {
            elementDescription = Delegation.elementDescription(call, location);
        } else if (org.elixir_lang.structure_view.element.Exception.is(call)) {
            elementDescription = org.elixir_lang.structure_view.element.Exception.elementDescription(call, location);
        } else if (Implementation.is(call)) {
            elementDescription = Implementation.elementDescription(call, location);
        } else if (Import.is(call)) {
            elementDescription = Import.elementDescription(call, location);
        } else if (Module.is(call)) {
            elementDescription = Module.elementDescription(call, location);
        } else if (Overridable.is(call)) {
            elementDescription = Overridable.elementDescription(call, location);
        } else if (Protocol.is((call))) {
            elementDescription = Protocol.elementDescription(call, location);
        } else if (org.elixir_lang.structure_view.element.Quote.is(call)) {
            elementDescription = org.elixir_lang.structure_view.element.Quote.elementDescription(call, location);
        } else if (Structure.is(call)) {
            elementDescription = Structure.elementDescription(call, location);
        } else if (Type.is(call)) {
            elementDescription = Type.elementDescription(call, location);
        } else if (Use.is(call)) {
            elementDescription = Use.elementDescription(call, location);
        } else if (call instanceof AtUnqualifiedNoParenthesesCall) {
            elementDescription = getElementDescription((AtUnqualifiedNoParenthesesCall) call, location);
        } else if (Callable.isBitStreamSegmentOption(call)) {
            elementDescription = Callable.bitStringSegmentOptionElementDescription(call, location);
        } else if (Callable.isIgnored(call)) {
            elementDescription = Callable.ignoredElementDescription(call, location);
        } else if (Callable.isParameter(call)) {
            elementDescription = Callable.parameterElementDescription(call, location);
        } else if (Callable.isParameterWithDefault(call)) {
            elementDescription = Callable.parameterWithDefaultElementDescription(call, location);
        } else if (Callable.isVariable(call)) {
            elementDescription = Callable.variableElementDescription(call, location);
        } else {
            if (location == UsageViewTypeLocation.INSTANCE) {
                elementDescription = "call";
            }
        }

        return elementDescription;
    }

    private String getElementDescription(@NotNull AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall,
                                         ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewLongNameLocation.INSTANCE || location == UsageViewShortNameLocation.INSTANCE) {
            elementDescription = atUnqualifiedNoParenthesesCall.getName();
        } else if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "module attribute";
        }

        return elementDescription;
    }

    private boolean isAliasCallArgument(@NotNull Call element) {
        return element.isCalling(KERNEL, ALIAS);
    }

    private boolean isAliasCallArgument(@NotNull PsiElement element) {
        boolean isAliasCallArgument = false;

        if (element instanceof Call) {
            isAliasCallArgument = isAliasCallArgument((Call) element);
        } else if (element instanceof Arguments ||
                element instanceof ElixirAccessExpression ||
                element instanceof ElixirMultipleAliases ||
                element instanceof QualifiableAlias ||
                element instanceof QualifiedMultipleAliases) {
            isAliasCallArgument = isAliasCallArgument(element.getParent());
        } else if (element instanceof QuotableKeywordPair) {
            isAliasCallArgument = isAliasCallAs((QuotableKeywordPair) element);
        }

        return isAliasCallArgument;
    }

    private boolean isAliasCallAs(@NotNull QuotableKeywordPair element) {
        boolean isAliasCallArgument = false;

        if (hasKeywordKey(element, "as")) {
            PsiElement parent = element.getParent();

            if (parent instanceof QuotableKeywordList) {
                PsiElement grandParent = parent.getParent();

                isAliasCallArgument = isAliasCallArgument(grandParent);
            }
        }

        return isAliasCallArgument;
    }

    private boolean isAliasCallAs(@NotNull PsiElement element) {
        boolean isAliasCallAs = false;

        if (element instanceof ElixirAccessExpression ||
                element instanceof QualifiableAlias) {
            isAliasCallAs = isAliasCallAs(element.getParent());
        } else if (element instanceof QuotableKeywordPair) {
            isAliasCallAs = isAliasCallAs((QuotableKeywordPair) element);
        }

        return isAliasCallAs;
    }
}
