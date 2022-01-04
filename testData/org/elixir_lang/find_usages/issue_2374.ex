defmodule Foo do
  def foo, do: "foo"
  def bar, do: f<caret>oo()
  def baz, do: foo()
end
