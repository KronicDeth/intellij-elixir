defmodule ParameterInfo.PrefixMatchDeclaration do
  def reduce(enumerable, fun), do: {enumerable, fun}
  def reduce(enumerable, acc, fun), do: {enumerable, acc, fun}
  def reduce_while(enumerable, acc, fun), do: {enumerable, acc, fun}
end
