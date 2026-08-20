defmodule RenameMod do
  def renamed_foo(a), do: a
  def foo(a, b), do: a + b

  def call_it do
    renamed_foo(1) + foo(1, 2)
  end
end
