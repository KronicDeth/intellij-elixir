package org.elixir_lang.psi;

import com.intellij.ide.scratch.ScratchFileServiceImpl;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewNodeTextLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import com.intellij.util.Function;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.call.name.Function.IMPORT;
import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.finalArguments;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.fullyResolveAlias;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.maybeAliasToModular;

/**
 * An {@code import} call
 */
public class Import {
    /*
     * CONSTANTS
     */

    private static final Function<Call, Boolean> TRUE = new Function<Call, Boolean>() {
        @Contract(pure = true)
        @NotNull
        @Override
        public Boolean fun(@NotNull @SuppressWarnings("unused") Call call) {
            return true;
        }
    };

    /*
     * Public Static Methods
     */

    /**
     * Calls {@code function} on each call definition clause imported by {@code importCall} while {@code function}
     * returns {@code true}.  Stops the first time {@code function} returns {@code false}
     *
     * @param importCall an {@code import} {@link Call} (should have already been checked with {@link #is(Call)}.
     * @param function For {@code import Module}, called on all call definition clauses in {@code Module}; for
     *   {@code import Module, only: [...]} called on only the call definition clauses matching names in
     *   {@code :only} list; for {@code import Module, except: [...]} called on all call definition clauses expect those
     *   matching names in {@code :except} list.
     */
    public static void callDefinitionClauseCallWhile(@NotNull Call importCall,
                                                     @NotNull final Function<Call, Boolean> function) {
        Call modularCall = modular(importCall);

        if (modularCall != null) {
            final Function<Call, Boolean> optionsFilter = callDefinitionClauseCallFilter(importCall);

            Modular.callDefinitionClauseCallWhile(
                    modularCall,
                    new Function<Call, Boolean>() {
                        @Override
                        public Boolean fun(Call call) {
                            return !optionsFilter.fun(call) || function.fun(call);
                        }
                    }
            );
        }
    }

    @Nullable
    public static String elementDescription(@NotNull Call call, @NotNull ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "import";
        } else if (location == UsageViewNodeTextLocation.INSTANCE) {
            elementDescription = call.getText();
        }

        return elementDescription;
    }

    /**
     * Whether {@code call} is an {@code import Module} or {@code import Module, opts} call
     */
    public static boolean is(@NotNull Call call) {
        boolean is = false;

        if (call.isCalling(KERNEL, IMPORT)) {
            int resolvedFinalArity = call.resolvedFinalArity();

            if (1 <= resolvedFinalArity && resolvedFinalArity <= 2) {
                is = true;
            }
        }

        return is;
    }

    /*
     * Private Static Methods
     */

    /**
     * A {@link Function} that returns {@code true} for call definition clauses that are imported by {@code importCall}
     *
     * @param importCall {@code import} call
     */
    @NotNull
    private static Function<Call, Boolean> callDefinitionClauseCallFilter(@NotNull Call importCall) {
        PsiElement[] finalArguments = finalArguments(importCall);
        Function<Call, Boolean> filter = TRUE;

        if (finalArguments != null && finalArguments.length > 2) {
            filter = optionsCallDefinitionClauseCallFilter(finalArguments[0]);
        }

        return filter;
    }

    /**
     * The modular that is imported by {@code importCall}.
     * @param importCall a {@link Call} where {@link #is} is {@code true}.
     * @return {@code defmodule}, {@code defimpl}, or {@code defprotocol} imported by {@code importCall}.  It can be
     *   {@code null} if Alias passed to {@code importCall} cannot be resolved.
     */
    @Nullable
    private static Call modular(@NotNull Call importCall) {
        PsiElement[] finalArguments = finalArguments(importCall);
        Call modular = null;

        if (finalArguments != null && finalArguments.length >= 1) {
            modular = maybeAliasToModular(finalArguments[0]);
        }

        return modular;
    }


    /**
     * A {@link Function} that returns {@code true} for call definition clauses that are imported by {@code importCall}
     *
     * @param options options (second argument) to an {@code import Module, ...} call.
     */
    @Nullable
    private static Function<Call, Boolean> optionsCallDefinitionClauseCallFilter(@Nullable PsiElement options) {
        Function<Call, Boolean> filter = TRUE;

        if (options != null) {
            assert options != null;
        }

        return filter;
    }
}
