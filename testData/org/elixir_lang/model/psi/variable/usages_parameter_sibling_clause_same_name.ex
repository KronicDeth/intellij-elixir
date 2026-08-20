defmodule ParameterSiblingClause do
  def run(va<caret>lue), do: value
  def run(value) when is_integer(value), do: value
end
