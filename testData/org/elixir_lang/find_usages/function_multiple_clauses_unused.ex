defmodule Unused do
  def fun<caret>ction(list) when is_list(list), do: []
  def function(map) when is_map(map), do: %{}
end
