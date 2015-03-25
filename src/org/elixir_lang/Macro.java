package org.elixir_lang;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;

public class Macro {
    public static OtpErlangList callArguments(OtpErlangTuple callExpression) {
        return (OtpErlangList) callExpression.elementAt(2);
    }

    public static boolean isAliases(OtpErlangObject macro) {
        boolean aliases = false;

        if (macro instanceof OtpErlangTuple) {
            OtpErlangTuple expression = (OtpErlangTuple) macro;

            if (expression.arity() == 3) {
                OtpErlangObject first = expression.elementAt(0);

                if (first instanceof OtpErlangAtom) {
                    OtpErlangAtom firstAtom = (OtpErlangAtom) first;

                    if (firstAtom.atomValue() == "__aliases__") {
                        aliases = true;
                    }
                }
            }
        }

        return aliases;
    }

    public static OtpErlangList metadata(OtpErlangTuple expression) {
        return (OtpErlangList) expression.elementAt(1);
    }
}
