package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.DO;
import static org.elixir_lang.psi.impl.QuotableImpl.NIL;

public class QuotableArgumentsImpl {
    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final Arguments arguments) {
        PsiElement[] unquotedArguments = arguments.arguments();
        OtpErlangObject[] quotedArguments = new OtpErlangObject[unquotedArguments.length];

        for (int i = 0; i < unquotedArguments.length; i++) {
            Quotable quotableArgument = (Quotable) unquotedArguments[i];
            quotedArguments[i] = quotableArgument.quote();
        }

        return quotedArguments;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final ElixirBlockList blockList) {
        List<ElixirBlockItem> blockItemList = blockList.getBlockItemList();
        OtpErlangObject[] quotedBlockItems = new OtpErlangObject[blockItemList.size()];

        int i = 0;
        for (ElixirBlockItem blockItem : blockItemList) {
            quotedBlockItems[i++] = blockItem.quote();
        }

        return quotedBlockItems;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(ElixirUnqualifiedNoParenthesesManyArgumentsCall unqualifiedNoParenthesesManyArgumentsCall) {
        PsiElement[] primaryArguments = unqualifiedNoParenthesesManyArgumentsCall.primaryArguments();
        OtpErlangObject[] quotedArguments = new OtpErlangObject[primaryArguments.length];

        for (int i = 0; i< primaryArguments.length; i++) {
            Quotable quotablePrimaryArgument = (Quotable) primaryArguments[i];
            quotedArguments[i] = quotablePrimaryArgument.quote();
        }

        return quotedArguments;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final ElixirDoBlock doBlock) {
        ElixirStab stab = doBlock.getStab();
        OtpErlangObject doValue = NIL;
        OtpErlangObject[] quotedKeywordPairs;

        if (stab != null) {
            doValue = stab.quote();
        }

        OtpErlangTuple quotedDoKeywordPair = new OtpErlangTuple(
                new OtpErlangObject[]{
                        DO,
                        doValue
                }
        );

        ElixirBlockList blockList = doBlock.getBlockList();

        if (blockList != null) {
            OtpErlangObject[] blockListQuotedArguments = blockList.quoteArguments();

            quotedKeywordPairs = new OtpErlangObject[1 + blockListQuotedArguments.length];

            int i = 0;
            quotedKeywordPairs[i++] = quotedDoKeywordPair;
            System.arraycopy(blockListQuotedArguments, 0, quotedKeywordPairs, i, blockListQuotedArguments.length);
        } else {
            quotedKeywordPairs = new OtpErlangObject[]{
                    quotedDoKeywordPair
            };
        }

        return new OtpErlangObject[]{
                new OtpErlangList(quotedKeywordPairs)
        };
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final ElixirMapConstructionArguments mapConstructionArguments) {
        PsiElement[] arguments = mapConstructionArguments.arguments();
        List<OtpErlangObject> quotedArgumentList = new ArrayList<>();

        for (PsiElement argument : arguments) {
            Quotable quotableArgument = (Quotable) argument;
            OtpErlangList quotedArgument = (OtpErlangList) quotableArgument.quote();

            quotedArgumentList.addAll(Arrays.asList(quotedArgument.elements()));
        }

        OtpErlangObject[] quotedArguments = new OtpErlangObject[quotedArgumentList.size()];

        return quotedArgumentList.toArray(quotedArguments);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(@NotNull final ElixirNoParenthesesArguments noParenthesesArguments) {
        PsiElement[] children = noParenthesesArguments.getChildren();

        assert children.length == 1;

        QuotableArguments quotableArguments = (QuotableArguments) children[0];
        return quotableArguments.quoteArguments();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject[] quoteArguments(ElixirParenthesesArguments parenthesesArguments) {
        PsiElement[] arguments = parenthesesArguments.arguments();

        OtpErlangObject[] quotedArguments = new OtpErlangObject[arguments.length];

        for (int i = 0; i < arguments.length; i++) {
            Quotable quotableChild = (Quotable) arguments[i];
            OtpErlangObject quotedChild = quotableChild.quote();
            quotedArguments[i] = quotedChild;
        }

        return quotedArguments;
    }
}
