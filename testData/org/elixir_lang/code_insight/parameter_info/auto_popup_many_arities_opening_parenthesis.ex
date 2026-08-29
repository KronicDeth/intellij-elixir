defmodule ParameterInfo.AutoPopupManyAritiesOpeningParenthesis do
  def run do
    reduce<caret>
  end

  def reduce(enumerable, fun), do: {enumerable, fun}
  def reduce(enumerable, acc, fun), do: {enumerable, acc, fun}
  def reduce(enumerable, acc, transform, fun), do: {enumerable, acc, transform, fun}
end
