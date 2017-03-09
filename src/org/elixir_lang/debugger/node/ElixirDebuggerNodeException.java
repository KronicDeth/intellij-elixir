package org.elixir_lang.debugger.node;

public class ElixirDebuggerNodeException extends Exception {
  public ElixirDebuggerNodeException(String message) {
    super(message);
  }

  public ElixirDebuggerNodeException(String message, Throwable cause) {
    super(message, cause);
  }
}