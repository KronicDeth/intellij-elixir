defmodule RecursiveFamily do
  def perform([], acc), do: acc

  def per<caret>form([h | t], acc) do
    perform(t, [h | acc])
  end
end
