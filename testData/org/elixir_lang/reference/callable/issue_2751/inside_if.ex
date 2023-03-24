defmodule Foo do
  def foo do
    if true do
      b<caret>
    end
  end

  def bar do
    "bar"
  end

  def baz do
    "baz"
  end
end
