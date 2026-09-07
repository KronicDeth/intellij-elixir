defmodule GuardRead do
  def size(c) do
    fresh = 1

    case c do
      y when fresh > y -> :small
      _ -> :big
    end
  end
end
