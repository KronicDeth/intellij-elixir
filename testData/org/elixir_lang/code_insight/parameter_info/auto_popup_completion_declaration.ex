defmodule ParameterInfo.CompletionRemote do
  def reduce(enumerable, fun), do: {enumerable, fun}
  def reduce_while(enumerable, acc, fun), do: {enumerable, acc, fun}
end
