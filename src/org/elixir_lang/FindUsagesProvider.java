package org.elixir_lang;

import com.intellij.find.FindManager;
import com.intellij.find.impl.HelpID;
import com.intellij.lang.cacheBuilder.SimpleWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.MaybeModuleName;
import org.elixir_lang.psi.QualifiableAlias;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.java.generate.inspection.AbstractToStringInspection;

public class FindUsagesProvider implements com.intellij.lang.findUsages.FindUsagesProvider {
    /**
     * Gets the word scanner for building a word index for the specified language.
     * Note that the implementation MUST be thread-safe, otherwise you should return a new instance of your scanner
     * (that can be recommended as a best practice).
     *
     * @return the word scanner implementation, or null if {@link SimpleWordsScanner} is OK.
     */
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return null;
    }

    /**
     * Checks if it makes sense to search for usages of the specified element.
     *
     * @param psiElement the element for which usages are searched.
     * @return true if the search is allowed, false otherwise.
     * @see FindManager#canFindUsages(PsiElement)
     */
    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        boolean canFindUsages = false;

        if (psiElement instanceof AtUnqualifiedNoParenthesesCall) {
            canFindUsages = true;
        } else if (psiElement instanceof MaybeModuleName) {
            MaybeModuleName maybeModuleName = (MaybeModuleName) psiElement;

            canFindUsages = maybeModuleName.isModuleName();
        }

        return canFindUsages;
    }

    /**
     * Returns the ID of the help topic which is shown when the specified element is selected
     * in the "Find Usages" dialog.
     *
     * @param psiElement the element for which the help topic is requested.
     * @return the help topic ID, or null if no help is available.
     */
    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        String helpId = null;

        if (psiElement instanceof AtUnqualifiedNoParenthesesCall) {
            helpId = com.intellij.lang.HelpID.FIND_OTHER_USAGES;
        } if (psiElement instanceof MaybeModuleName) {
            MaybeModuleName maybeModuleName = (MaybeModuleName) psiElement;

            if (maybeModuleName.isModuleName()) {
                // TODO double check wording of help makes sense or if a special HelpID should be created for Modules
                helpId = HelpID.FIND_CLASS_USAGES;
            }
        }

        return helpId;
    }

    /**
     * Returns the user-visible type of the specified element, shown in the "Find Usages"
     * dialog (for example, "class" or "variable"). The type name should not be upper-cased.
     *
     * @param element the element for which the type is requested.
     * @return the type of the element.
     */
    @NotNull
    @Override
    public String getType(@NotNull final PsiElement element) {
        // Intentionally use `null` to trigger `@NotNull` when a new element type is passed.
        String type = null;

        if (element instanceof AtUnqualifiedNoParenthesesCall) {
            type = "module attribute";
        } if (element instanceof MaybeModuleName) {
            MaybeModuleName maybeModuleName = (MaybeModuleName) element;

            if (maybeModuleName.isModuleName()) {
                type = "module";
            }
        }

        //noinspection ConstantConditions
        return type;
    }

    /**
     * Returns an expanded user-visible name of the specified element, shown in the "Find Usages"
     * dialog. For classes, this can return a fully qualified name of the class; for methods -
     * a signature of the method with parameters.
     *
     * @param element the element for which the name is requested.
     * @return the user-visible name.
     */
    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        // Intentionally use `null` to trigger `@NotNull` when a new element type is passed.
        String descriptiveName = null;

        if (element instanceof AtUnqualifiedNoParenthesesCall) {
            AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall = (AtUnqualifiedNoParenthesesCall) element;

            descriptiveName = atUnqualifiedNoParenthesesCall.moduleAttributeName();
        } else if (element instanceof ElixirFile) {
            ElixirFile file = (ElixirFile) element;

            descriptiveName = file.getVirtualFile().getPresentableUrl();
        } else if (element instanceof MaybeModuleName) {
            MaybeModuleName maybeModuleName = (MaybeModuleName) element;

            if (maybeModuleName.isModuleName()) {
                if (maybeModuleName instanceof QualifiableAlias) {
                    QualifiableAlias qualifiableAlias = (QualifiableAlias) maybeModuleName;

                    descriptiveName = qualifiableAlias.fullyQualifiedName();
                }
            }
        }

        //noinspection ConstantConditions
        return descriptiveName;
    }

    /**
     * Returns the text representing the specified PSI element in the Find Usages tree.
     *
     * @param element     the element for which the node text is requested.
     * @param useFullName if true, the returned text should use fully qualified names
     * @return the text representing the element.
     */
    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        // Intentionally use `null` to trigger `@NotNull` when a new element type is passed.
        String nodeText = null;

        if (element instanceof AtUnqualifiedNoParenthesesCall) {
            AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall = (AtUnqualifiedNoParenthesesCall) element;
            nodeText = atUnqualifiedNoParenthesesCall.getName();
        } else if (element instanceof MaybeModuleName) {
            MaybeModuleName maybeModuleName = (MaybeModuleName) element;

            if (maybeModuleName.isModuleName()) {
                if (useFullName) {
                    if (maybeModuleName instanceof QualifiableAlias) {
                        QualifiableAlias qualifiableAlias = (QualifiableAlias) maybeModuleName;

                        nodeText = qualifiableAlias.fullyQualifiedName();
                    }
                } else {
                    nodeText = maybeModuleName.getText();
                }
            }
        }

        //noinspection ConstantConditions
        return nodeText;
    }
}
