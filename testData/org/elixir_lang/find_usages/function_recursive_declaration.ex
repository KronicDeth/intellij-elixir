defmodule Recursive do
  def function([], acc), do: acc

  def fun<caret>ction([h | t], acc) do
    function(t, [h | acc])
  end
end
