package org.elixir_lang.annonator;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.elixir_lang.errorreport.Logger;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.operation.InMatch;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.Delegation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Parameter {
    public static enum Type {
        FUNCTION_NAME,
        MACRO_NAME,
        VARIABLE
    }

    @Contract(pure = true)
    @Nullable
    public static Type type(@NotNull PsiElement ancestor) {
        PsiElement parent = ancestor.getParent();
        Type type = null;

        if (parent instanceof Call) {
            type = type((Call) parent);
        } else if (parent instanceof AtNonNumericOperation ||
                parent instanceof ElixirAccessExpression ||
                parent instanceof ElixirAssociations ||
                parent instanceof ElixirAssociationsBase ||
                parent instanceof ElixirBitString ||
                parent instanceof ElixirBracketArguments ||
                parent instanceof ElixirContainerAssociationOperation ||
                parent instanceof ElixirKeywordPair ||
                parent instanceof ElixirKeywords ||
                parent instanceof ElixirList ||
                parent instanceof ElixirMapArguments ||
                parent instanceof ElixirMapConstructionArguments ||
                parent instanceof ElixirMapOperation ||
                parent instanceof ElixirMatchedParenthesesArguments ||
                parent instanceof ElixirNoParenthesesArguments ||
                parent instanceof ElixirNoParenthesesKeywordPair ||
                parent instanceof ElixirNoParenthesesKeywords ||
                /* ElixirNoParenthesesManyStrictNoParenthesesExpression indicates a syntax error where no parentheses
                   calls are nested, so it's invalid, but try to still resolve parameters to have highlighting */
                parent instanceof ElixirNoParenthesesManyStrictNoParenthesesExpression ||
                parent instanceof ElixirNoParenthesesOneArgument ||
                /* handles `(conn, %{})` in `def (conn, %{})`, which can occur in def templates.
                   See https://github.com/KronicDeth/intellij-elixir/issues/367#issuecomment-244214975 */
                parent instanceof ElixirNoParenthesesStrict ||
                parent instanceof ElixirParenthesesArguments ||
                parent instanceof ElixirParentheticalStab ||
                parent instanceof ElixirStab ||
                parent instanceof ElixirStabNoParenthesesSignature ||
                parent instanceof ElixirStabBody ||
                parent instanceof ElixirStabOperation ||
                parent instanceof ElixirStabParenthesesSignature ||
                parent instanceof ElixirStructOperation ||
                parent instanceof ElixirTuple) {
            type = type(parent);
        } else if (parent instanceof ElixirAnonymousFunction || parent instanceof InMatch) {
            type = Type.VARIABLE;
        } else {
            if (!(parent instanceof BracketOperation ||
                    parent instanceof ElixirBlockItem ||
                    parent instanceof ElixirDoBlock ||
                    parent instanceof ElixirInterpolation ||
                    parent instanceof ElixirMapUpdateArguments ||
                    parent instanceof ElixirQuoteStringBody ||
                    parent instanceof PsiFile ||
                    parent instanceof QualifiedAlias ||
                    parent instanceof QualifiedMultipleAliases)) {
                error("Don't know how to check if parameter", parent);
            }
        }

        return type;
    }

    /*
     * Private Static Methods
     */

    private static void error(@NotNull String message, @NotNull PsiElement element) {
        Logger.error(Parameter.class, message + " (when element class is " + element.getClass().getName() + ")", element);
    }

    private static Type type(@NotNull Call call) {
        Type type;

        if (CallDefinitionClause.isFunction(call) || Delegation.is(call)) {
            type = Type.FUNCTION_NAME;
        } else if (CallDefinitionClause.isMacro(call)) {
            type = Type.MACRO_NAME;
        } else if (call.hasDoBlockOrKeyword()) {
            type = Type.VARIABLE;
        } else {
            // use generic handling, so that parent is checked
            type = type((PsiElement) call);
        }

        return type;
    }
}
