defmodule VariableCaseClauseRebinding do
  def run(other) do
    fresh = 0

    case other do
      :inc ->
        fresh = fresh + 1
        fresh

      _ ->
        fresh
    end
  end
end
