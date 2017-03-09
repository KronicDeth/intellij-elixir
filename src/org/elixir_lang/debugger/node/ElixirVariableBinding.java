package org.elixir_lang.debugger.node;

import com.ericsson.otp.erlang.OtpErlangObject;

public class ElixirVariableBinding {
  private final String myName;
  private final OtpErlangObject myValue;

  public ElixirVariableBinding(String name, OtpErlangObject value) {
    myName = name;
    myValue = value;
  }

  public String getName() {
    return myName;
  }

  public OtpErlangObject getValue() {
    return myValue;
  }
}
