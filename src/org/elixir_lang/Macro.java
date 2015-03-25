package org.elixir_lang;

import com.ericsson.otp.erlang.*;

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
}
