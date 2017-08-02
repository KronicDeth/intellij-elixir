package org.elixir_lang.reference.resolver;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.psi.AtNonNumericOperation;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.ElixirAtIdentifier;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.psi.scope.module_attribute.implemetation.For;
import org.elixir_lang.psi.scope.module_attribute.implemetation.Protocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.elixir_lang.psi.scope.MultiResolve.HAS_VALID_RESULT_CONDITION;
import static org.elixir_lang.reference.ModuleAttribute.isNonReferencing;

public class ModuleAttribute implements ResolveCache.PolyVariantResolver<org.elixir_lang.reference.ModuleAttribute> {
    public static final ModuleAttribute INSTANCE = new ModuleAttribute();

    @NotNull
    private static List<ResolveResult> multiResolveSibling(
            @NotNull org.elixir_lang.reference.ModuleAttribute moduleAttribute,
            @Nullable PsiElement lastSibling,
            boolean incompleteCode
    ) {
        List<ResolveResult> resultList = new ArrayList<>();

        for (PsiElement sibling = lastSibling; sibling != null; sibling = sibling.getPrevSibling()) {
            if (sibling instanceof AtUnqualifiedNoParenthesesCall) {
                AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall =
                        (AtUnqualifiedNoParenthesesCall) sibling;
                String moduleAttributeName = ElixirPsiImplUtil.moduleAttributeName(atUnqualifiedNoParenthesesCall);
                String value = moduleAttribute.getValue();

                if (moduleAttributeName.equals(value)) {
                    resultList.add(new PsiElementResolveResult(atUnqualifiedNoParenthesesCall));
                } else if (incompleteCode && moduleAttributeName.startsWith(value)) {
                    resultList.add(new PsiElementResolveResult(atUnqualifiedNoParenthesesCall, false));
                }
            }
        }

        return resultList;
    }

    @NotNull
    private static List<ResolveResult> multiResolveUpFromElement(
            @NotNull org.elixir_lang.reference.ModuleAttribute moduleAttribute,
            @NotNull final PsiElement element,
            boolean incompleteCode
    ) {
        List<ResolveResult> resultList = new ArrayList<>();
        PsiElement lastSibling = element;

        while (lastSibling != null) {
            resultList.addAll(multiResolveSibling(moduleAttribute, lastSibling, incompleteCode));

            lastSibling = lastSibling.getParent();
        }

        return resultList;
    }

    @Nullable
    private static Boolean validResult(@NotNull org.elixir_lang.reference.ModuleAttribute moduleAttribute,
                                       @NotNull String moduleAttributeName,
                                       boolean incompleteCode) {
        Boolean validResult = null;
        String value = moduleAttribute.getValue();

        if (value.equals(moduleAttributeName)) {
            validResult = true;
        } else if (incompleteCode && moduleAttributeName.startsWith(value)) {
            validResult = false;
        }

        return validResult;
    }

    @NotNull
    @Override
    public ResolveResult[] resolve(@NotNull org.elixir_lang.reference.ModuleAttribute moduleAttribute, boolean incompleteCode) {
        List<ResolveResult> resultList = new ArrayList<>();
        boolean isNonReferencing = false;
        PsiElement element = moduleAttribute.getElement();

        if (element instanceof AtNonNumericOperation) {
            isNonReferencing = isNonReferencing((AtNonNumericOperation) element);
        } else if (element instanceof ElixirAtIdentifier) {
            isNonReferencing = isNonReferencing((ElixirAtIdentifier) element);
        }

        if (!isNonReferencing) {
            Boolean validResult;

            validResult = validResult(moduleAttribute, "@protocol", incompleteCode);

            if (validResult != null) {
                List<ResolveResult> resolveResultList = Protocol.resolveResultList(validResult, element);

                if (resolveResultList != null) {
                    resultList.addAll(resolveResultList);
                }
            }

            if (incompleteCode || !ContainerUtil.exists(resultList, HAS_VALID_RESULT_CONDITION)) {
                validResult = validResult(moduleAttribute, "@for", incompleteCode);

                if (validResult != null) {
                    List<ResolveResult> resolveResultList = For.resolveResultList(validResult, element);

                    if (resolveResultList != null) {
                        resultList.addAll(resolveResultList);
                    }
                }

                if (incompleteCode || !ContainerUtil.exists(resultList, HAS_VALID_RESULT_CONDITION)) {
                    resultList.addAll(multiResolveUpFromElement(moduleAttribute, element, incompleteCode));
                }
            }
        }

        return resultList.toArray(new ResolveResult[resultList.size()]);
    }

}
