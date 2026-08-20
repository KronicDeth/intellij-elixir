defmodule RenameMod do
  def fo<caret>o(a), do: a
  def foo(a, b), do: a + b

  def call_it do
    foo(1) + foo(1, 2)
  end
end
