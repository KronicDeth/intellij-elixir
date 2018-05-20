defmodule Recursive do
  def function([], acc), do: acc

  def function([h | t], acc) do
    fun<caret>ction(t, [h | acc])
  end
end
