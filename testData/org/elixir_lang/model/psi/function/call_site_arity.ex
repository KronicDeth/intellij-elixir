defmodule Arity do
  def foo(a), do: a
  def foo(a, b), do: a + b

  def call do
    fo<caret>o(1)
  end
end
