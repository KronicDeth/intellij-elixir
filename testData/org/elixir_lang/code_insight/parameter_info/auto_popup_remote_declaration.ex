defmodule ParameterInfo.Remote do
  def reduce(enumerable, fun), do: {enumerable, fun}
  def reduce(enumerable, acc, fun), do: {enumerable, acc, fun}
end
