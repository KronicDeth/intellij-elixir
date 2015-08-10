package org.elixir_lang;

import com.ericsson.otp.erlang.*;
import com.google.common.base.Joiner;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Macro {
    public static OtpErlangList callArguments(OtpErlangTuple callExpression) {
        return (OtpErlangList) callExpression.elementAt(2);
    }

    public static boolean isAliases(OtpErlangObject macro) {
        boolean aliases = false;

        if (isExpression(macro)) {
            OtpErlangTuple expression = (OtpErlangTuple) macro;

            OtpErlangObject first = expression.elementAt(0);

            if (first instanceof OtpErlangAtom) {
                OtpErlangAtom firstAtom = (OtpErlangAtom) first;

                if (firstAtom.atomValue().equals("__aliases__")) {
                    aliases = true;
                }
            }
        }

        return aliases;
    }

    /**
     * Return whether quoted form contains an Elixir keyword that is just an alias to an atom, such as `false`,
     * `true`, or `nil`.
     *
     * @param macro a quoted form from a {@code quote} method.
     * @return {@code true} if OtpErlangAtom containing one of the keywords.
     */
    public static boolean isAtomKeyword(OtpErlangObject macro) {
        boolean atomKeyword = false;

        for (OtpErlangAtom knownAtomKeyword : ElixirPsiImplUtil.ATOM_KEYWORDS) {
            if (macro.equals(knownAtomKeyword)) {
                atomKeyword = true;

                break;
            }
        }

        return atomKeyword;
    }

    /**
     * Return whether the macro is a __block__ expression representing sequential lines.
     *
     * @param macro a quoted form from a {@code quote} method.
     * @return {@code true} if `{:__block__, _, _}`.
     */
    public static boolean isBlock(OtpErlangObject macro) {
        boolean block = false;

        if (isExpression(macro)) {
            OtpErlangTuple expression = (OtpErlangTuple) macro;

            if (expression.elementAt(0).equals(ElixirPsiImplUtil.BLOCK)) {
                block = true;
            }
        }

        return block;
    }

    /**
     * Return whether the macro is an Expr node: `expr :: {expr | atom, Keyword.t, atom | [t]}`.
     *
     * @param macro a quoted form from a {@code quote} method.
     * @return {@code true} if a tuple with 3 elements; {@code false} otherwise.
     */
    public static boolean isExpression(OtpErlangObject macro) {
        boolean expression = false;

        if (macro instanceof OtpErlangTuple) {
            OtpErlangTuple tuple = (OtpErlangTuple) macro;

            if (tuple.arity() == 3) {
                expression = true;
            }
        }

        return expression;
    }

    /**
     * Returns whether macro is a local call.
     *
     * @param macro a quoted form
     * @return {@code true} if local call; {@code false} otherwise.
     * @see <a href="https://github.com/elixir-lang/elixir/blob/6151f2ab1af0189b9c8c526db196e2a65c609c64/lib/elixir/lib/macro.ex#L277-L281">Macro.decompose_call/1</a>
     */
    public static boolean isLocalCall(OtpErlangObject macro) {
        boolean localCall = false;

        if (isExpression(macro)) {
            OtpErlangTuple expression = (OtpErlangTuple) macro;

            OtpErlangObject first = expression.elementAt(0);

            if (first instanceof OtpErlangAtom) {
                OtpErlangObject last = expression.elementAt(2);

                /* OtpErlangString maps to CharList, which are list, so is_list in Elixir would be true for
                   OtpErlangList and OtpErlangString. */
                if (last instanceof OtpErlangList || last instanceof OtpErlangString) {
                    localCall = true;
                }
            }
        }

        return localCall;
    }

    /** Return whether macro is a local call expression with no arguments.
     *
     * @param macro
     * @return
     */
    public static boolean isVariable(OtpErlangObject macro) {
        boolean variable = false;

        if (isExpression(macro)) {
            OtpErlangTuple expression = (OtpErlangTuple) macro;

            OtpErlangObject first = expression.elementAt(0);

            if (first instanceof OtpErlangAtom) {
                OtpErlangObject last = expression.elementAt(2);

                if (last instanceof OtpErlangAtom) {
                    variable = true;
                }
            }
        }

        return variable;
    }

    public static OtpErlangList metadata(OtpErlangTuple expression) {
        return (OtpErlangList) expression.elementAt(1);
    }

    /**
     *
     * @param macro
     * @return
     * @see <a href="https://github.com/elixir-lang/elixir/blob/a1b06e1e9067ae08826f91274fefcb68c2bbdd02/lib/elixir/lib/macro.ex#L461-L464">Macro.to_string</a>
     */
    @NotNull
    public static String toString(OtpErlangObject macro) {
        assert Macro.isAliases(macro);

        OtpErlangTuple quotedAlias = (OtpErlangTuple) macro;
        OtpErlangList refs = (OtpErlangList) quotedAlias.elementAt(2);
        java.util.List<String> refStringList = new ArrayList<String>();

        for (OtpErlangObject ref : refs) {
            OtpErlangAtom refAtom = (OtpErlangAtom) ref;

            refStringList.add(refAtom.atomValue());
        }

        return Joiner.on(".").join(refStringList);
    }
}
