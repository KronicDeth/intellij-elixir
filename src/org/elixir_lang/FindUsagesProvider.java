package org.elixir_lang;

import com.intellij.find.FindManager;
import com.intellij.find.impl.HelpID;
import com.intellij.lang.cacheBuilder.SimpleWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.ElementDescriptionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewNodeTextLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

        if (psiElement instanceof Call) {
            canFindUsages = true;
        } else if (psiElement instanceof MaybeModuleName) {
            MaybeModuleName maybeModuleName = (MaybeModuleName) psiElement;

            canFindUsages = maybeModuleName.isModuleName();
        }

        return canFindUsages;
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
        return ElementDescriptionUtil.getElementDescription(element, UsageViewLongNameLocation.INSTANCE);
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
        }
        if (psiElement instanceof MaybeModuleName) {
            MaybeModuleName maybeModuleName = (MaybeModuleName) psiElement;

            if (maybeModuleName.isModuleName()) {
                // TODO double check wording of help makes sense or if a special HelpID should be created for Modules
                helpId = HelpID.FIND_CLASS_USAGES;
            }
        }

        return helpId;
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
        String nodeText;

        if (useFullName) {
            nodeText = ElementDescriptionUtil.getElementDescription(element, UsageViewLongNameLocation.INSTANCE);
        } else {
            nodeText = ElementDescriptionUtil.getElementDescription(element, UsageViewNodeTextLocation.INSTANCE);
        }

        return nodeText;
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
            return ElementDescriptionUtil.getElementDescription(element, UsageViewTypeLocation.INSTANCE);
    }
}
