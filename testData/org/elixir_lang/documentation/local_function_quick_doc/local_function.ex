defmodule Documented do
  @doc "Adds two numbers together."
  def add(a, b) do
    a + b
  end

  def caller do
    ad<caret>d(1, 2)
  end
end
