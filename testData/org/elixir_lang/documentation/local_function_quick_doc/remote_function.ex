defmodule Callee do
  @moduledoc "Callee module."
  @doc "Multiplies two numbers."
  def multiply(a, b) do
    a * b
  end
end

defmodule Caller do
  def run do
    Callee.mul<caret>tiply(2, 3)
  end
end
