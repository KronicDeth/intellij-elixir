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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.util.Pair.pair;

public class CallDefinitionClause extends Element<Call> {
    /*
     * Constants
     */

    /*
     * Fields
     */

    private final CallDefinition callDefinition;

    /*
     * Public Static Methods
     */

    public static boolean isFunction(Call call) {
        return call.isCallingMacro("Elixir.Kernel", "def", 2) ||
                // function head
                call.isCalling("Elixir.Kernel", "def", 1);
    }

    public static boolean isMacro(Call call) {
        return call.isCallingMacro("Elixir.Kernel", "defmacro", 2) ||
                // macro head
                call.isCalling("Elixir.Kernel", "defmacro", 1);
    }

    /**
     * The name and arity of the macro this clause belongs to.
     *
     * @param call
     * @return The name and arity of the {@code Macro} this macro clause belongs
     */
    @NotNull
    public static Pair<String, Integer> nameArity(Call call) {
        PsiElement[] primaryArguments = call.primaryArguments();

        assert primaryArguments != null;
        assert primaryArguments.length > 0;

        PsiElement head = primaryArguments[0];
        String name = null;
        Integer arity = null;
        Call headCall = null;

        if (head instanceof ElixirMatchedWhenOperation) {
            ElixirMatchedWhenOperation whenOperation = (ElixirMatchedWhenOperation) head;

            headCall = (Call) whenOperation.leftOperand();
        } else if (head instanceof Call) {
            headCall = (Call) head;
        } else if (head instanceof ElixirAccessExpression) {
            PsiElement[] headChildren = head.getChildren();

            if (headChildren.length == 1) {
                PsiElement headChild = headChildren[0];

                if (headChild instanceof ElixirParentheticalStab) {
                    ElixirParentheticalStab parentheticalStab = (ElixirParentheticalStab) headChild;

                    PsiElement[] parentheticalStabChildren = parentheticalStab.getChildren();

                    if (parentheticalStabChildren.length == 1) {
                        PsiElement parentheticalStabChild = parentheticalStabChildren[0];

                        if (parentheticalStabChild instanceof ElixirStab) {
                            ElixirStab stab = (ElixirStab) parentheticalStabChild;

                            PsiElement[] stabChildren = stab.getChildren();

                            if (stabChildren.length == 1) {
                                PsiElement stabChild = stabChildren[0];

                                if (stabChild instanceof ElixirStabBody) {
                                    ElixirStabBody stabBody = (ElixirStabBody) stabChild;

                                    PsiElement[] stabBodyChildren = stabBody.getChildren();

                                    if (stabBodyChildren.length == 1) {
                                        PsiElement stabBodyChild = stabBodyChildren[0];

                                        if (stabBodyChild instanceof Call) {
                                            headCall = (Call) stabBodyChild;
                                        } else {
                                            openIssueForNotImplemented(
                                                    "Macro clauses defined with `Elixir.Kernel.defmacro/2` are assumed " +
                                                            "to have a Call in ElixirStabBody, but got " +
                                                            stabBodyChild.getClass().getCanonicalName() + ".",
                                                    head
                                            );
                                        }
                                    } else {
                                        openIssueForNotImplemented(
                                                "Macro clauses defined with `Elixir.Kernel.defmacro/2` are assumed to " +
                                                        "have a single child for ElixirStabBody, but got " +
                                                        stabBodyChildren.length + ".",
                                                head
                                        );
                                    }
                                } else {
                                    openIssueForNotImplemented(
                                            "Macro clauses defined with `Elixir.Kernel.defmacro/2` are assumed to have " +
                                                    "an ElixirStabBody in ElixirStab, but got " +
                                                    stabChild.getClass().getCanonicalName() + ".",
                                            head
                                    );
                                }
                            } else {
                                openIssueForNotImplemented(
                                        "Macro clauses defined with `Elixir.Kernel.defmacro/2` are assumed to have an " +
                                                "one child in the ElixirStab, but got " + stabChildren.length + ". ",
                                        head
                                );
                            }
                        } else {
                            openIssueForNotImplemented(
                                    "Macro clauses defined with `Elixir.Kernel.defmacro/2` are assumed to have an " +
                                            "ElixirStab under ElixirParentheticalStab, but got " +
                                            parentheticalStabChild.getClass().getCanonicalName() + ".",
                                    head
                            );
                        }
                    } else {
                        openIssueForNotImplemented(
                                "Macro clauses defined with `Elixir.Kernel.defmacro/2` are assumed to have one child " +
                                        "for ElixirParentheticalStab, but got " + parentheticalStabChildren.length +
                                        ".",
                                head
                        );
                    }
                } else {
                    openIssueForNotImplemented(
                            "Macro clauses defined with `Elixir.Kernel.defmacro/2` are assumed to have " +
                                    "ElixirParentheticalStab for ElixirAccessExpression, but got " +
                                    headChild.getClass().getCanonicalName() + ".",
                            head
                    );
                }
            } else {
                openIssueForNotImplemented(
                        "Macro clauses defined with `Elixir.Kernel.defmacro/2` are assumed to have one child for " +
                                "ElixirAccessExpression, but got " + headChildren.length + " children.",
                        head
                );
            }
        } else if (head instanceof ElixirMatchedAtNonNumericOperation) {
            ElixirMatchedAtNonNumericOperation matchedAtNonNumericOperation = (ElixirMatchedAtNonNumericOperation) head;

            name = matchedAtNonNumericOperation.operator().getText().trim();
            arity = 1;
        } else {
            openIssueForNotImplemented(
                    "Macro clauses defined with `Elixir.Kernel.defmacro/2` are assumed to have either Calls or " +
                            "MatchedWhenOperation as the first primaryArgument, but got " +
                            head.getClass().getCanonicalName() + ".",
                    head
            );
        }

        if (headCall != null) {
            name = headCall.functionName();
            arity = headCall.resolvedFinalArity();
        }

        return pair(name, arity);
    }

    /**
     * Throws {@code NotImplementedException} with the description and instructions to open an issue.
     *
     * @param description description of the unexpected condition that needs to be handled in the implementation.
     * @param element     Element whose text to include in the issue to open
     */
    public static void openIssueForNotImplemented(String description, PsiElement element) {
        throw new NotImplementedException(
                description + " Please open an issue (https://github.com/KronicDeth/intellij-elixir/issues/new) with " +
                        "the sample text:\n" + element.getText()
        );
    }

    /*
     * Constructors
     */

    public CallDefinitionClause(CallDefinition callDefinition, Call call) {
        super(call);
        this.callDefinition = callDefinition;
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

            PsiElement[] stabChildren = stab.getChildren();

            if (stabChildren.length == 1) {
                PsiElement stabChild = stabChildren[0];

                if (stabChild instanceof ElixirStabBody) {
                    ElixirStabBody stabBody = (ElixirStabBody) stabChild;
                    Call[] childCalls = PsiTreeUtil.getChildrenOfType(stabBody, Call.class);

                    if (childCalls != null) {
                        List<TreeElement> treeElementList = new ArrayList<TreeElement>(childCalls.length);

                        for (Call childCall : childCalls) {
                            if (Implementation.is(childCall)) {
                                treeElementList.add(new Implementation(callDefinition.getModule(), childCall));
                            } else if (Module.is(childCall)) {
                                treeElementList.add(new Module(callDefinition.getModule(), childCall));
                            }
                        }

                        children = treeElementList.toArray(new TreeElement[treeElementList.size()]);
                    }
                }
            }
        } else { // one liner version with `do:` keyword argument
            PsiElement[] finalArguments = ElixirPsiImplUtil.finalArguments(navigationItem);

            assert finalArguments.length > 0;

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

                        if (Implementation.is(childCall)) {
                            children = new TreeElement[]{
                                    new Implementation(callDefinition.getModule(), childCall)
                            };
                        } else if (Module.is(childCall)) {
                            children = new TreeElement[]{
                                    new Module(callDefinition.getModule(), childCall)
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
        return new org.elixir_lang.navigation.item_presentation.CallDefinitionClause(
                (org.elixir_lang.navigation.item_presentation.CallDefinition) callDefinition.getPresentation(),
                navigationItem
        );
    }

}

