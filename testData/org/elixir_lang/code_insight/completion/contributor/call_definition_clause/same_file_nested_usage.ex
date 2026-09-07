defmodule Outer do
  defmodule Inner do
    def inner_function(a), do: a
  end

  def run do
    Inner.<caret>
    Inner.inner_function(1)
  end
end
