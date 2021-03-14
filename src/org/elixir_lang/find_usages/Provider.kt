package org.elixir_lang.find_usages

import com.intellij.find.FindManager
import com.intellij.find.impl.HelpID
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.psi.ElementDescriptionUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.usageView.UsageViewLongNameLocation
import com.intellij.usageView.UsageViewNodeTextLocation
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.ElixirLexer
import org.elixir_lang.ElixirParserDefinition
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.psi.*
import org.elixir_lang.psi.ElixirTypes.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.isOutermostQualifiableAlias
import org.elixir_lang.reference.Callable

private val IDENTIFIER_TOKEN_SET = TokenSet.create(
        ALIAS_TOKEN,
        AND_SYMBOL_OPERATOR,
        AND_WORD_OPERATOR,
        ARROW_OPERATOR,
        COMPARISON_OPERATOR,
        DIVISION_OPERATOR,
        IDENTIFIER_TOKEN,
        MINUS_OPERATOR,
        MULTIPLICATION_OPERATOR,
        NEGATE_OPERATOR,
        NOT_OPERATOR,
        NUMBER_OR_BADARITH_OPERATOR,
        OR_SYMBOL_OPERATOR,
        OR_WORD_OPERATOR,
        PIPE_OPERATOR,
        PLUS_OPERATOR,
        RANGE_OPERATOR,
        SIGN_OPERATOR,
        THREE_OPERATOR,
        TWO_OPERATOR,
        UNARY_OPERATOR
)

class Provider : com.intellij.lang.findUsages.FindUsagesProvider {
    /**
     * Gets the word scanner for building a word index for the specified language.
     * Note that the implementation MUST be thread-safe, otherwise you should return a new instance of your scanner
     * (that can be recommended as a best practice).
     *
     * @return the word scanner implementation.
     */
    override fun getWordsScanner(): WordsScanner? =
            DefaultWordsScanner(
                    ElixirLexer(),
                    IDENTIFIER_TOKEN_SET,
                    ElixirParserDefinition.COMMENTS,
                    ElixirParserDefinition.STRING_LITERALS
            )

    /**
     * Checks if it makes sense to search for usages of the specified element.
     *
     * @param psiElement the element for which usages are searched.
     * @return true if the search is allowed, false otherwise.
     * @see FindManager.canFindUsages
     */
    override fun canFindUsagesFor(psiElement: PsiElement): Boolean =
            when (psiElement) {
                is AtNonNumericOperation, is ModuleImpl<*> -> true
                is Call ->
                    /* On a definer, the position of the caret is important to determine whether the thing being defined
                       or the definer macro's usage should be found */
                    !(Callable.isDefiner(psiElement)  ||
                            // Don't find usage for the `name` in `@name`.  `AtNonNumericOperation` above will instead
                            // be used for all of `@name.
                            psiElement.isModuleAttributeNameElement())
                is QualifiableAlias -> psiElement.isOutermostQualifiableAlias()
                else -> false
            }

    /**
     * Returns an expanded user-visible name of the specified element, shown in the "Find Usages"
     * dialog. For classes, this can return a fully qualified name of the class; for methods -
     * a signature of the method with parameters.
     *
     * @param element the element for which the name is requested.
     * @return the user-visible name.
     */
    override fun getDescriptiveName(element: PsiElement): String =
            ElementDescriptionUtil.getElementDescription(element, UsageViewLongNameLocation.INSTANCE)

    /**
     * Returns the ID of the help topic which is shown when the specified element is selected
     * in the "Find Usages" dialog.
     *
     * @param psiElement the element for which the help topic is requested.
     * @return the help topic ID, or null if no help is available.
     */
    override fun getHelpId(psiElement: PsiElement): String? =
            when (psiElement) {
                is AtUnqualifiedNoParenthesesCall<*> ->
                    com.intellij.lang.HelpID.FIND_OTHER_USAGES
                is MaybeModuleName ->
                    if (psiElement.isModuleName) {
                        // TODO double check wording of help makes sense or if a special HelpID should be created for Modules
                        HelpID.FIND_CLASS_USAGES
                    } else {
                        null
                    }
                else -> null
            }

    /**
     * Returns the text representing the specified PSI element in the Find Usages tree.
     *
     * @param element     the element for which the node text is requested.
     * @param useFullName if true, the returned text should use fully qualified names
     * @return the text representing the element.
     */
    override fun getNodeText(element: PsiElement, useFullName: Boolean): String =
            if (useFullName) {
                ElementDescriptionUtil.getElementDescription(element, UsageViewLongNameLocation.INSTANCE)
            } else {
                ElementDescriptionUtil.getElementDescription(element, UsageViewNodeTextLocation.INSTANCE)
            }

    /**
     * Returns the user-visible type of the specified element, shown in the "Find Usages"
     * dialog (for example, "class" or "variable"). The type name should not be upper-cased.
     *
     * @param element the element for which the type is requested.
     * @return the type of the element.
     */
    override fun getType(element: PsiElement): String =
            ElementDescriptionUtil.getElementDescription(element, UsageViewTypeLocation.INSTANCE)
}
