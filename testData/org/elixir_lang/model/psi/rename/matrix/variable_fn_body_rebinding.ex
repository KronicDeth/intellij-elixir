defmodule VariableFnBodyRebinding do
  def run(list) do
    renamee = 0

    Enum.each(list, fn _item ->
      renamee = renamee + 1
      renamee
    end)

    renamee
  end
end
