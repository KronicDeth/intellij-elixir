defmodule Target do
  def map(enumerable, fun), do: Enum.map(enumerable, fun)
end

defmodule Usage do
  def example(arity) do
    {Target, :ma<caret>p, arity}
  end
end
