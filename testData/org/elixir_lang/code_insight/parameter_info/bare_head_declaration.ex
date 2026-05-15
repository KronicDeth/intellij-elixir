defmodule ParameterInfo.BareHeadDeclaration do
  def map_every(enumerable, nth, fun)

  def map_every(enumerable, 1, fun), do: {enumerable, fun}
  def map_every([], nth, _fun) when is_integer(nth) and nth > 1, do: []

  def map_every(enumerable, nth, fun) when is_integer(nth) and nth > 1 do
    {enumerable, nth, fun}
  end
end
