defmodule Foo do
  def foo do
    if true do
    end
    b<caret>
  end

  def bar do
    "bar"
  end

  def baz do
    "baz"
  end
end
