package org.elixir_lang.annonator;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
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
    public enum Type {
        FUNCTION_NAME,
        MACRO_NAME,
        VARIABLE;

        public static boolean isCallDefinitionClauseName(Type type) {
            return type == FUNCTION_NAME || type == MACRO_NAME;
        }
    }

    /*
     * Public Static Methods
     */

    /**
     * A new {@link Parameter} with {@link #parameterized} filled in if {@code parameter}'s {@link #entrance} is a
     * parameter element.
     *
     * @return a new {@link Parameter} with {@link #parameterized} filled in if {@link #entrance} is a valida parameter
     *   element.
     */
    @Contract(pure = true)
    @NotNull
    public static Parameter putParameterized(final @NotNull Parameter parameter) {
        return putParameterized(parameter, parameter.entrance);
    }

    /*
     * Private Static Methods
     */

    @Contract(pure = true)
    @NotNull
    private static <T> T notNullize(@Nullable T nullable, @NotNull T defaultValue) {
        T notNull;

        if (nullable == null) {
            notNull = defaultValue;
        } else {
            notNull = nullable;
        }

        return notNull;
    }

    private static void error(@NotNull String message, @NotNull PsiElement element) {
        Logger.error(Parameter.class, message + " (when element class is " + element.getClass().getName() + ")", element);
    }

    @Contract(pure = true)
    @NotNull
    private static Parameter putParameterized(@NotNull final Parameter parameter, final @NotNull Call ancestor) {
        Parameter parameterizedParameter;

        if (CallDefinitionClause.isFunction(ancestor) || Delegation.is(ancestor)) {
            parameterizedParameter = new Parameter(
                    parameter.defaultValue,
                    parameter.entrance,
                    notNullize(parameter.parameterized, ancestor),
                    notNullize(parameter.type, Type.FUNCTION_NAME)
            );
        } else if (CallDefinitionClause.isMacro(ancestor)) {
            parameterizedParameter = new Parameter(
                    parameter.defaultValue,
                    parameter.entrance,
                    notNullize(parameter.parameterized, ancestor),
                    notNullize(parameter.type, Type.MACRO_NAME)
            );
        } else if (ancestor.hasDoBlockOrKeyword()) {
            parameterizedParameter = new Parameter(
                    parameter.defaultValue,
                    parameter.entrance,
                    ancestor,
                    notNullize(parameter.type, Type.VARIABLE)
            );
        } else {
            PsiElement element = ancestor.functionNameElement();
            Parameter updatedParameter = parameter;

            if (!PsiTreeUtil.isAncestor(element, parameter.entrance, false)) {
                updatedParameter = new Parameter(
                        parameter.defaultValue,
                        parameter.entrance,
                        ancestor,
                        notNullize(parameter.type, Type.VARIABLE)
                );
            }

            // use generic handling so that parent is checked
            parameterizedParameter = putParameterized(updatedParameter, (PsiElement) ancestor);
        }

        return parameterizedParameter;
    }

    @Contract(pure = true)
    @NotNull
    private static Parameter putParameterized(@NotNull final Parameter parameter,
                                              @NotNull final ElixirAnonymousFunction ancestor) {
        return new Parameter(
                parameter.defaultValue,
                parameter.entrance,
                ancestor,
                notNullize(parameter.type, Type.VARIABLE)
        );
    }

    @Contract(pure = true)
    @NotNull
    private static Parameter putParameterized(@NotNull final Parameter parameter, @NotNull final PsiElement ancestor) {
        Parameter parameterizedParameter;
        PsiElement parent = ancestor.getParent();

        if (parent instanceof Call) {
            parameterizedParameter = putParameterized(parameter, (Call) parent);
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
            parameterizedParameter = putParameterized(parameter, parent);
        } else if (parent instanceof ElixirAnonymousFunction) {
            parameterizedParameter = putParameterized(parameter, (ElixirAnonymousFunction) parent);
        } else if (parent instanceof InMatch) {
            parameterizedParameter = putParameterized(parameter, (InMatch) parent);
        } else if (parent instanceof BracketOperation ||
                    parent instanceof ElixirBlockItem ||
                    parent instanceof ElixirDoBlock ||
                    parent instanceof ElixirInterpolation ||
                    parent instanceof ElixirMapUpdateArguments ||
                    parent instanceof ElixirQuoteStringBody ||
                    parent instanceof PsiFile ||
                    parent instanceof QualifiedAlias ||
                    parent instanceof QualifiedMultipleAliases) {
            parameterizedParameter = new Parameter(parameter.entrance);
        } else {
            error("Don't know how to check if parameter", parent);
            parameterizedParameter = new Parameter(parameter.entrance);
        }

        return parameterizedParameter;
    }

    /*
     * Fields
     */

    @Nullable
    public final PsiElement defaultValue;
    @NotNull
    public final PsiElement entrance;
    @Nullable
    public final NavigatablePsiElement parameterized;
    @Nullable
    public final Type type;

    /*
     * Constructors
     */

    public Parameter(@NotNull PsiElement entrance) {
        this.defaultValue = null;
        this.entrance = entrance;
        this.parameterized = null;
        this.type = null;
    }

    private Parameter(@Nullable PsiElement defaultValue,
                      @NotNull PsiElement entrance,
                      @Nullable NavigatablePsiElement parameterized,
                      @Nullable Type type) {
        this.defaultValue = defaultValue;
        this.entrance = entrance;
        this.parameterized = parameterized;
        this.type = type;
    }

    /*
     * Public Instance Methods
     */

    /**
     * Whether the {@link #type} is call definition clause name
     *
     * @return {@code true} if {@link #type} is {@link Type#FUNCTION_NAME} or {@link Type#MACRO_NAME}.
     */
    @Contract(pure = true)
    public boolean isCallDefinitionClauseName() {
        return Type.isCallDefinitionClauseName(type);
    }

    /**
     * Whether {@link #entrance} represents a parameter to a {@link #parameterized} element
     * @return {@code true} if {@link #parameterized} is not {@code null}
     */
    @Contract(pure = true)
    boolean isValid() {
        return parameterized == null;
    }

    /**
     * A Parameter that is not a parameter to anything.
     *
     * @param parameter The original {@link Parameter} that may or may not be parameterized
     * @return an invalid parameter
     */
    @NotNull
    public Parameter not(final @NotNull Parameter parameter) {
        Parameter not;

        if (parameter.defaultValue == null && parameter.parameterized == null) {
            not = parameter;
        } else {
            not = new Parameter(parameter.entrance);
        }

        return not;
    }
}
