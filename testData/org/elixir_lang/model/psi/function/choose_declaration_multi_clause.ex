defmodule ChooseDeclarationMultiClause do
  def perform([], acc), do: acc

  def perform([h | t], acc) do
    perform(t, [h | acc])
  end

  def run(list), do: per<caret>form(list, [])
end
