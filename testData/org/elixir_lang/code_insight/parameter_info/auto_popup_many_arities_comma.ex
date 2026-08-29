defmodule ParameterInfo.AutoPopupManyAritiesComma do
  def run do
    reduce(1<caret>)
  end

  def reduce(enumerable, fun), do: {enumerable, fun}
  def reduce(enumerable, acc, fun), do: {enumerable, acc, fun}
  def reduce(enumerable, acc, transform, fun), do: {enumerable, acc, transform, fun}
end
