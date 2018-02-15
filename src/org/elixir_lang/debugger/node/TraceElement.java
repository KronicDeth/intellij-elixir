package org.elixir_lang.debugger.node;

import com.ericsson.otp.erlang.OtpErlangList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class TraceElement {
  private final String myModule;
  private final String myFunction;
  private final OtpErlangList myFunctionArgs;
  private final Collection<VariableBinding> myBindings;
  private final String myFile;
  private final int myLine;

  public TraceElement(@NotNull String module, @NotNull String function, @NotNull OtpErlangList functionArgs,
                      @NotNull Collection<VariableBinding> bindings, String file, int line) {
    myModule = module;
    myFunction = function;
    myFunctionArgs = functionArgs;
    myBindings = bindings;
    myFile = file;
    myLine = line;
  }

  @NotNull
  public String getModule() {
    return myModule;
  }

  @NotNull
  public String getFunction() {
    return myFunction;
  }

  @NotNull
  public OtpErlangList getFunctionArgs() {
    return myFunctionArgs;
  }

  @NotNull
  public Collection<VariableBinding> getBindings() {
    return myBindings;
  }

  public String getFile() {
    return myFile;
  }

  public int getLine() {
    return myLine;
  }
}
