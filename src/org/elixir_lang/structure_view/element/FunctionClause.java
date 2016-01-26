package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
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

import static com.intellij.openapi.util.Pair.pair;

public class FunctionClause extends Element<Call> {
    /*
     * Fields
     */

    private final Function function;

    /*
     * Public Static Methods
     */

    public static boolean is(Call call) {
        return call.isCallingMacro("Elixir.Kernel", "def", 2) ||
                // function head
                call.isCalling("Elixir.Kernel", "def", 1);
    }

    /**
     * The name and arity of the function this clause belongs to.
     *
     * @param call
     * @return The name and arity of the {@code Function} this function clause belongs
     */
    @NotNull
    public static Pair<String, Integer> nameArity(Call call) {
        PsiElement[] primaryArguments = call.primaryArguments();

        assert primaryArguments != null;
        assert primaryArguments.length > 0;

        PsiElement head = primaryArguments[0];
        Call headCall;

        if (head instanceof Call) {
            headCall = (Call) head;
        } else if (head instanceof ElixirMatchedWhenOperation) {
            ElixirMatchedWhenOperation whenOperation = (ElixirMatchedWhenOperation) head;

            headCall = (Call) whenOperation.leftOperand();
        } else {
            throw new NotImplementedException(
                    "Function clauses defined with `Elixir.Kernel.def/2` are assumed to have either Calls or " +
                            "MatchedWhenOperation as the first primaryArgument, but got " +
                            head.getClass().getCanonicalName() + ". Please open an issue " +
                            "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the sample text:\n" +
                            head.getText()
            );
        }

        String name = headCall.functionName();
        Integer arity = headCall.resolvedFinalArity();

        return pair(name, arity);
    }

    /*
     * Constructors
     */

    public FunctionClause(Function function, Call call) {
        super(call);
        this.function = function;
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
                    treeElementList.add(new Module(function.getModule(), childCall));
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
                                    new Module(function.getModule(), childCall)
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
        return new org.elixir_lang.navigation.item_presentation.FunctionClause(
                (org.elixir_lang.navigation.item_presentation.Function) function.getPresentation(),
                navigationItem
        );
    }

}

