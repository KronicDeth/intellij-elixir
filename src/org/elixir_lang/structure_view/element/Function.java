package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Function extends Element<Call> {
    /*
     * Fields
     */

    private final Module module;

    /*
     * Public Static Methods
     */

    public static boolean is(Call call) {
        return call.isCallingMacro("Elixir.Kernel", "def", 2);
    }

    /*
     * Constructors
     */

    public Function(Module module, Call call) {
        super(call);
        this.module = module;
    }

    /*
     * Public Instance Methods
     */

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        ElixirDoBlock doBlock = navigationItem.getDoBlock();
        TreeElement[] children = null;

        if (doBlock != null) {
            ElixirStab stab = doBlock.getStab();

            Collection<Call> childCalls = PsiTreeUtil.findChildrenOfType(stab, Call.class);
            List<TreeElement> treeElementList = new ArrayList<TreeElement>(childCalls.size());

            for (Call childCall : childCalls) {
                // Kernel.def/2 can't be called in another Kernel.def/2, but one can call `Kernel.defmodule/2`
                if (Module.is(childCall)) {
                    treeElementList.add(new Module(module, childCall));
                }
            }

            children = treeElementList.toArray(new TreeElement[treeElementList.size()]);
        } else { // one liner version with `do:` keyword argument
            PsiElement[] finalArguments = ElixirPsiImplUtil.finalArguments(navigationItem);

            assert  finalArguments.length > 0;

            PsiElement potentialKeywords = finalArguments[finalArguments.length - 1];

            if (potentialKeywords instanceof QuotableKeywordList) {
                QuotableKeywordList quotableKeywordList = (QuotableKeywordList) potentialKeywords;
                List<QuotableKeywordPair> quotableKeywordPairList = quotableKeywordList.quotableKeywordPairList();
                QuotableKeywordPair firstQuotableKeywordPair = quotableKeywordPairList.get(0);
                Quotable keywordKey = firstQuotableKeywordPair.getKeywordKey();

                if (keywordKey.getText().equals("do")) {
                    Quotable keywordValue = firstQuotableKeywordPair.getKeywordValue();

                    if (keywordValue instanceof Call) {
                        Call childCall = (Call) keywordValue;

                        if (Module.is(childCall)) {
                            children = new TreeElement[] {
                                    new Module(module, childCall)
                            };
                        }
                    }
                }
            }
        }

        if (children == null) {
            children = new TreeElement[0];
        }

        return children;
    }

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new org.elixir_lang.navigation.item_presentation.Function(
                (org.elixir_lang.navigation.item_presentation.Module) module.getPresentation(),
                navigationItem
        );
    }

}

