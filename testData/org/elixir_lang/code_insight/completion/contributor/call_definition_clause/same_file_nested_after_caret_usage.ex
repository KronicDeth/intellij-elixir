defmodule Outer do
  def run do
    Inner.<caret>
  end

  defmodule Inner do
    def inner_function(a), do: a
  end
end
