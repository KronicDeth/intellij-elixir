defmodule Outer.Callee do
  @moduledoc "Callee module."
  @doc "Subtracts two numbers."
  def subtract(a, b) do
    a - b
  end
end

defmodule Caller do
  alias Outer.Callee, as: Renamed

  def run do
    Renamed.sub<caret>tract(5, 3)
  end
end
