package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import org.elixir_lang.Macro;

/**
 * Module definition wrapping a `defmodule` call PSIElement.
 */
public class ModuleDefinition {
    /*
     * Public Fields
     */

    public final Quotable defmodule;

    /*
     * Constructors
     */

    public ModuleDefinition(Quotable defmodule){
        this.defmodule = defmodule;
    }

    public String name() {
        OtpErlangTuple quotedDefmodule = (OtpErlangTuple) defmodule.quote();
        OtpErlangList callArguments = Macro.callArguments(quotedDefmodule);

        // Alias + block
        assert callArguments.arity() == 2;

        OtpErlangObject quotedName = callArguments.elementAt(0);

        // TODO handle other forms for module names
        assert Macro.isAliases(quotedName);

        return Macro.toString(quotedName);
    }

    public String fullyQualifiedName(){
        return "Elixir." + name();
    }
}
