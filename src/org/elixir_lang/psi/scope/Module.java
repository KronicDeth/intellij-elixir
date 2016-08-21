package org.elixir_lang.psi.scope;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Named;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.call.name.Function.ALIAS;
import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.finalArguments;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.keywordArgument;
import static org.elixir_lang.psi.stub.type.call.Stub.isModular;

public abstract class Module implements PsiScopeProcessor {
    /*
     * Public Instance Methods
     */

    @Override
    public boolean execute(@NotNull PsiElement match, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (match instanceof Named) {
            keepProcessing = execute((Named) match, state);
        }

        return keepProcessing;
    }

    @Nullable
    @Override
    public <T> T getHint(@NotNull Key<T> hintKey) {
        return null;
    }

    @Override
    public void handleEvent(@NotNull Event event, @Nullable Object associated) {
    }

    /*
     * Protected Instance Methods
     */

    /**
     * Decides whether {@code match} matches the criteria being searched for.  All other {@link #execute} methods
     * eventually end here.
     *
     * @return {@code true} to keep processing; {@code false} to stop processing.
     */
    protected abstract boolean executeOnAliasedName(@NotNull final PsiNamedElement match,
                                                    @NotNull String aliasedName,
                                                    @NotNull ResolveState state);

    /*
     * Private Instance Methods
     */

    @Nullable
    private String aliasedName(@NotNull ElixirAlias element) {
        return element.getName();
    }

    @Nullable
    private String aliasedName(@NotNull PsiElement element) {
        String aliasedName = null;

        if (element instanceof ElixirAlias) {
            aliasedName = aliasedName((ElixirAlias) element);
        } else if (element instanceof QualifiedAlias) {
            aliasedName = aliasedName((QualifiedAlias) element);
        }

        return aliasedName;
    }

    @Nullable
    private String aliasedName(@NotNull QualifiedAlias element) {
        String aliasedName = null;

        PsiElement[] children = element.getChildren();
        int operatorIndex = org.elixir_lang.psi.operation.Normalized.operatorIndex(children);
        PsiElement unqualified = org.elixir_lang.psi.operation.infix.Normalized.rightOperand(children, operatorIndex);        assert children.length == 3;

        if (unqualified instanceof PsiNamedElement) {
            PsiNamedElement namedElement = (PsiNamedElement) unqualified;
            aliasedName = namedElement.getName();
        }

        return aliasedName;
    }


    private boolean execute(@NotNull Named match, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (isModular(match)) {
            keepProcessing = executeOnMaybeAliasedName(match, match.getName(), state);
        } else if (match.isCalling(KERNEL, ALIAS)) {
            keepProcessing = executeOnAliasCall(match, state);
        }

        return keepProcessing;
    }

    private boolean executeOnAliasCall(@NotNull Named aliasCall, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        PsiElement asKeywordValue = keywordArgument(aliasCall, "as");

        if (asKeywordValue != null) {
            keepProcessing = executeOnAs(asKeywordValue, state);
        } else {
            PsiElement[] finalArguments = finalArguments(aliasCall);

            if (finalArguments != null && finalArguments.length > 0) {
                keepProcessing = executeOnAliasCallArgument(finalArguments[0], state);
            }
        }

        return keepProcessing;
    }

    private boolean executeOnAliasCallArgument(@NotNull ElixirAccessExpression accessExpression,
                                               @NotNull ResolveState state) {
        return executeOnAliasCallArgument(accessExpression.getChildren(), state);
    }

    private boolean executeOnAliasCallArgument(@NotNull ElixirMultipleAliases multipleAliases,
                                               @NotNull ResolveState state) {
        return executeOnMultipleAliasChildren(multipleAliases.getChildren(), state);
    }


    private boolean executeOnAliasCallArgument(@Nullable PsiElement element, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (element instanceof ElixirAccessExpression) {
            keepProcessing = executeOnAliasCallArgument((ElixirAccessExpression) element, state);
        } else if (element instanceof QualifiableAlias) {
            keepProcessing = executeOnAliasCallArgument((QualifiableAlias) element, state);
        } else if (element instanceof QualifiedMultipleAliases) {
            keepProcessing = executeOnAliasCallArgument((QualifiedMultipleAliases) element, state);
        }

        return keepProcessing;
    }

    private boolean executeOnAliasCallArgument(@NotNull PsiElement[] children, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        for (@Nullable PsiElement child : children) {
            if (child != null) {
                keepProcessing = executeOnAliasCallArgument(child, state);

                if (!keepProcessing) {
                    break;
                }
            }
        }

        return keepProcessing;
    }

    private boolean executeOnMultipleAliasChild(@NotNull ElixirAccessExpression child, ResolveState state) {
        PsiElement[] accessChildren = child.getChildren();
        boolean keepProcessing = true;

        if (accessChildren != null) {
            keepProcessing = executeOnMultipleAliasChild(accessChildren, state);
        }

        return keepProcessing;
    }

    private boolean executeOnMultipleAliasChild(@NotNull PsiElement child, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (child instanceof ElixirAccessExpression) {
            keepProcessing = executeOnMultipleAliasChild((ElixirAccessExpression) child, state);
        } else if (child instanceof QualifiableAlias) {
            keepProcessing = executeOnMultipleAliasChild((QualifiableAlias) child, state);
        }

        return keepProcessing;
    }

    private boolean executeOnMultipleAliasChild(@NotNull PsiElement[] elements, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        for (PsiElement element : elements) {
            if (element != null) {
                keepProcessing = executeOnMultipleAliasChild(element, state);

                if (!keepProcessing) {
                    break;
                }
            }
        }

        return keepProcessing;
    }

    private boolean executeOnMultipleAliasChild(@NotNull QualifiableAlias element, @NotNull ResolveState state) {
        return executeOnMaybeAliasedName(element, aliasedName(element), state);
    }

    private boolean executeOnMultipleAliasChildren(@Nullable PsiElement[] elements, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (elements != null) {
            for (PsiElement element : elements) {
                if (element != null) {
                    keepProcessing = executeOnMultipleAliasChild(element, state);

                    if (!keepProcessing) {
                        break;
                    }
                }
            }
        }

        return keepProcessing;
    }

    private boolean executeOnAliasCallArgument(@NotNull QualifiableAlias qualifiableAlias,
                                               @NotNull ResolveState state) {
        return executeOnMaybeAliasedName(qualifiableAlias, aliasedName(qualifiableAlias), state);
    }

    private boolean executeOnAliasCallArgument(@NotNull QualifiedMultipleAliases qualifiedMultipleAliases,
                                               @NotNull ResolveState state) {
        PsiElement[] children = qualifiedMultipleAliases.getChildren();
        int operatorIndex = org.elixir_lang.psi.operation.Normalized.operatorIndex(children);
        PsiElement unqualified = org.elixir_lang.psi.operation.infix.Normalized.rightOperand(children, operatorIndex);
        boolean keepProcessing = true;

        if (unqualified instanceof ElixirMultipleAliases) {
            keepProcessing = executeOnAliasCallArgument((ElixirMultipleAliases) unqualified, state);
        }

        return keepProcessing;
    }

    private boolean executeOnAs(ElixirAccessExpression asKeywordValue, @NotNull ResolveState state) {
        PsiElement[] children = asKeywordValue.getChildren();
        boolean keepProcessing = true;

        if (children.length > 0) {
            PsiElement child = children[0];

            keepProcessing = executeOnAs(child, state);
        }

        return keepProcessing;
    }

    private boolean executeOnAs(PsiElement asKeywordValue, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (asKeywordValue instanceof ElixirAccessExpression) {
            keepProcessing = executeOnAs((ElixirAccessExpression) asKeywordValue, state);
        } else if (asKeywordValue instanceof PsiNamedElement) {
            PsiNamedElement namedElement = (PsiNamedElement) asKeywordValue;
            keepProcessing = executeOnMaybeAliasedName(
                    namedElement,
                    namedElement.getName(),
                    state
            );
        }

        return keepProcessing;
    }

    private boolean executeOnMaybeAliasedName(@NotNull PsiNamedElement named,
                                                 @Nullable String aliasedName,
                                                 @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (aliasedName != null) {
            keepProcessing = executeOnAliasedName(named, aliasedName, state);
        }

        return keepProcessing;
    }

}
