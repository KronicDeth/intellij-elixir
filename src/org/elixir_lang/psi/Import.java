package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangRangeException;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewNodeTextLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import com.intellij.util.Function;
import gnu.trove.THashMap;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.errorreport.Logger;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elixir_lang.psi.call.name.Function.IMPORT;
import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.*;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange;

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

    @NotNull
    private static Map<String, List<Integer>> aritiesByNameFromNameByArityKeywordList(@NotNull final ElixirList list) {
        Map<String, List<Integer>> aritiesByName = new THashMap<String, List<Integer>>();

        PsiElement[] children = list.getChildren();

        if (children.length >= 1) {
            PsiElement lastChild = children[children.length - 1];

            if (lastChild instanceof QuotableKeywordList) {
                QuotableKeywordList quotableKeywordList = (QuotableKeywordList) lastChild;

                for (QuotableKeywordPair quotableKeywordPair : quotableKeywordList.quotableKeywordPairList()) {
                    String name = keywordKeyToName(quotableKeywordPair.getKeywordKey());
                    Integer arity = keywordValueToArity(quotableKeywordPair.getKeywordValue());

                    if (name != null && arity != null) {
                        List<Integer> arities = aritiesByName.get(name);

                        if (arities == null) {
                            arities = new ArrayList<Integer>();
                        }

                        arities.add(arity);

                        aritiesByName.put(name, arities);
                    }
                }
            }
        }

        return aritiesByName;
    }

    @NotNull
    private static Map<String, List<Integer>> aritiesByNameFromNameByArityKeywordList(PsiElement element) {
        Map<String, List<Integer>> aritiesByName = new THashMap<String, List<Integer>>();

        PsiElement stripped = stripAccessExpression(element);

        if (stripped instanceof ElixirList) {
            aritiesByName = aritiesByNameFromNameByArityKeywordList((ElixirList) stripped);
        }

        return aritiesByName;
    }

    /**
     * A {@link Function} that returns {@code true} for call definition clauses that are imported by {@code importCall}
     *
     * @param importCall {@code import} call
     */
    @NotNull
    private static Function<Call, Boolean> callDefinitionClauseCallFilter(@NotNull Call importCall) {
        PsiElement[] finalArguments = finalArguments(importCall);
        Function<Call, Boolean> filter = TRUE;

        if (finalArguments != null && finalArguments.length >= 2) {
            filter = optionsCallDefinitionClauseCallFilter(finalArguments[1]);
        }

        return filter;
    }

    @NotNull
    private static Function<Call, Boolean> exceptCallDefinitionClauseCallFilter(PsiElement element) {
        final Function<Call, Boolean> only = onlyCallDefinitionClauseCallFilter(element);
        return new Function<Call, Boolean>() {
            @Override
            public Boolean fun(Call call) {
                return !only.fun(call);
            }
        };
    }

    @Nullable
    private static String keywordKeyToName(@NotNull final Quotable keywordKey) {
        OtpErlangObject quotedKeywordKey = keywordKey.quote();
        String name = null;

        if (quotedKeywordKey instanceof OtpErlangAtom) {
            OtpErlangAtom keywordKeyAtom = (OtpErlangAtom) quotedKeywordKey;

            name = keywordKeyAtom.atomValue();
        }

        return name;
    }

    @Nullable
    private static Integer keywordValueToArity(@NotNull final Quotable keywordValue) {
        OtpErlangObject quotedKeywordValue = keywordValue.quote();
        Integer arity = null;

        if (quotedKeywordValue instanceof OtpErlangLong) {
            OtpErlangLong quotedKeywordValueLong = (OtpErlangLong) quotedKeywordValue;

            try {
                arity = quotedKeywordValueLong.intValue();
            } catch (OtpErlangRangeException e) {
                Logger.error(Import.class, "Arity in OtpErlangLong could not be downcast to an int", keywordValue);
            }
        }

        return arity;
    }

    @NotNull
    private static Function<Call, Boolean> onlyCallDefinitionClauseCallFilter(PsiElement element) {
        final Map<String, List<Integer>> aritiesByName = aritiesByNameFromNameByArityKeywordList(element);

        return new Function<Call, Boolean>() {
            @Override
            public Boolean fun(Call call) {
                Pair<String, IntRange> callNameArityRange = nameArityRange(call);
                boolean include = false;

                if (callNameArityRange != null) {
                    String callName = callNameArityRange.first;

                    if (callName != null) {
                        List<Integer> arities = aritiesByName.get(callName);

                        if (arities != null) {
                            IntRange callArityRange = callNameArityRange.second;

                            for (int arity : arities) {
                                if (callArityRange.containsInteger(arity)) {
                                    include = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                return include;
            }
        };
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

        if (options != null && options instanceof QuotableKeywordList) {
            QuotableKeywordList keywordList = (QuotableKeywordList) options;

            for (QuotableKeywordPair quotableKeywordPair : keywordList.quotableKeywordPairList()) {
                if (hasKeywordKey(quotableKeywordPair, "except")) {
                    filter = exceptCallDefinitionClauseCallFilter(quotableKeywordPair.getKeywordValue());
                } else if (hasKeywordKey(quotableKeywordPair, "only")) {
                    filter = onlyCallDefinitionClauseCallFilter(quotableKeywordPair.getKeywordValue());
                }
            }
        }

        return filter;
    }
}
