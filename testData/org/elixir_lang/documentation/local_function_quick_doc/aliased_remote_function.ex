defmodule Outer.Callee do
  @moduledoc "Callee module."
  @doc "Divides two numbers."
  def divide(a, b) do
    div(a, b)
  end
end

defmodule Caller do
  alias Outer.Callee

  def run do
    Callee.div<caret>ide(6, 3)
  end
end
