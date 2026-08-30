defmodule VariableFnBodyRebinding do
  def run(list) do
    fresh = 0

    Enum.each(list, fn _item ->
      fresh = fresh + 1
      fresh
    end)

    fresh
  end
end
