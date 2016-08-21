package org.elixir_lang.reference.module;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.ElixirAccessExpression;
import org.elixir_lang.psi.ElixirMultipleAliases;
import org.elixir_lang.psi.QualifiableAlias;
import org.elixir_lang.psi.QualifiedMultipleAliases;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.StubBased;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.elixir_lang.Module.concat;
import static org.elixir_lang.psi.call.name.Function.__MODULE__;
import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.enclosingModularMacroCall;

public class ResolvableName {
    /*
     * Public Static Methods
     */

    /**
     * The full name of the qualifiable alias, with any multiple aliases expanded
     */
    @Nullable
    public static String resolvableName(@NotNull QualifiableAlias qualifiableAlias) {
        String resolvableName = qualifiableAlias.fullyQualifiedName();
        List<String> tail = null;

        if (resolvableName != null) {
            tail = new ArrayList<String>();
            tail.add(resolvableName);
        }

        return up(qualifiableAlias.getParent(), tail);
    }

    /*
     * Private Static Methods
     */

    @Nullable
    private static List<String> down(@NotNull Call qualifier) {
        List<String> nameList = null;

        if (qualifier.isCalling(KERNEL, __MODULE__, 0)) {
            Call enclosingCall = enclosingModularMacroCall(qualifier);

            if (enclosingCall != null && enclosingCall instanceof StubBased) {
                StubBased enclosingStubBasedCall = (StubBased) enclosingCall;
                String canonicalName = enclosingStubBasedCall.canonicalName();

                if (canonicalName != null) {
                    nameList = new ArrayList<String>();
                    nameList.add(canonicalName);
                }
            }
        }

        return nameList;
    }


    @Nullable
    private static List<String> down(@NotNull QualifiableAlias qualifier) {
        String resolvableName = qualifier.getName();
        List<String> nameList = null;

        if (resolvableName != null) {
            nameList = new ArrayList<String>();
            nameList.add(resolvableName);
        }

        return nameList;
    }

    @Nullable
    private static List<String> down(@NotNull PsiElement qualifier) {
        List<String> nameList = null;

        if (qualifier instanceof Call) {
            nameList = down((Call) qualifier);
        } else if (qualifier instanceof ElixirAccessExpression) {
            nameList = down(qualifier.getChildren());
        } else if (qualifier instanceof QualifiableAlias) {
            nameList = down((QualifiableAlias) qualifier);
        }

        return nameList;
    }

    @Nullable
    private static List<String> down(@NotNull PsiElement[] qualifiers) {
        List<String> nameList = null;

        for (PsiElement qualifier : qualifiers) {
            List<String> qualifierNameList = down(qualifier);

            if (qualifierNameList != null) {
                if (nameList == null) {
                    nameList = new ArrayList<String>(qualifierNameList.size());
                }

                nameList.addAll(qualifierNameList);
            }
        }

        return nameList;
    }

    @Nullable
    private static String up(@Nullable PsiElement ancestor, @Nullable List<String> tail) {
        String resolvableName = null;

        if (ancestor instanceof ElixirAccessExpression ||
                ancestor instanceof ElixirMultipleAliases) {
            resolvableName = up(ancestor.getParent(), tail);
        } else if (ancestor instanceof QualifiedMultipleAliases) {
            resolvableName = up((QualifiedMultipleAliases) ancestor, tail);
        } else if (tail != null) {
            resolvableName = concat(tail);
        }

        return resolvableName;
    }

    @Nullable
    private static String up(@NotNull QualifiedMultipleAliases ancestor, @Nullable List<String> tail) {
        PsiElement[] children = ancestor.getChildren();
        int operatorIndex = org.elixir_lang.psi.operation.Normalized.operatorIndex(children);

        PsiElement qualifier = org.elixir_lang.psi.operation.infix.Normalized.leftOperand(children, operatorIndex);
        List<String> qualifierNameList = null;

        if (qualifier != null) {
            qualifierNameList = down(qualifier);
        }

        List<String> nameList;

        if (qualifierNameList != null) {
            nameList = qualifierNameList;

            if (tail != null) {
                qualifierNameList.addAll(tail);
            }
        } else {
            nameList = tail;
        }

        String resolvableName = null;

        if (nameList != null) {
            resolvableName = concat(nameList);
        }

        return resolvableName;
    }

}
