package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.intellij.psi.PsiElement;
import org.elixir_lang.Macro;

/**
 * Module definition wrapping a `defmodule` call PSIElement.
 */
public class ModuleDefinition {
    /*
     * Private Fields
     */

    private final Quotable defmodule;

    /*
     * Constructors
     */

    public ModuleDefinition(Quotable defmodule){
        this.defmodule = defmodule;
    }

    public String fullyQualifiedName(){
        OtpErlangTuple quotedDefmodule = (OtpErlangTuple) defmodule.quote();
        OtpErlangList callArguments = Macro.callArguments(quotedDefmodule);

        // Alias + block
        assert callArguments.arity() == 2;

        OtpErlangObject quotedName = callArguments.elementAt(0);

        // TODO handle other forms for module names
        assert Macro.isAliases(quotedName);

        return "Elixir." + Macro.toString(quotedName);
    }
}
