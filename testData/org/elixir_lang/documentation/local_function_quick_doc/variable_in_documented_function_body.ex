defmodule Documented do
  @doc "Adds two numbers together."
  def add(a, b) do
    su<caret>m = a + b
    sum
  end
end
