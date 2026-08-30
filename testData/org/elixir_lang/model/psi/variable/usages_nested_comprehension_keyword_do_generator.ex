defmodule NestedComprehension do
  def run(rows) do
    for row <- rows, do: for(ce<caret>ll <- row, do: cell + 1)
  end
end
