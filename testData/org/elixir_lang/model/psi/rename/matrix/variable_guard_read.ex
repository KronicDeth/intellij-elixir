defmodule GuardRead do
  def size(c) do
    renamee = 1

    case c do
      y when renamee > y -> :small
      _ -> :big
    end
  end
end
