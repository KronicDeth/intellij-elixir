defmodule IntelliJElixir.Debugger.Interpreted do
  @moduledoc """
  Module only here to check that it is interpreted, since `IntelliJElixir.Debugger.Server` will not interpret itself.
  """

  def debuggable do
    IO.puts("debuggable line")
  end
end
