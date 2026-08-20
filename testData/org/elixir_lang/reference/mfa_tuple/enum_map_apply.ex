defmodule Enum do
  def map(enumerable, fun), do: fun
  def map_size(value), do: value
end

defmodule Usage do
  def example do
    apply(Enum, :ma<caret>p, [[], fn x -> x end])
  end
end
