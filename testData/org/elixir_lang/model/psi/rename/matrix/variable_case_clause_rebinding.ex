defmodule VariableCaseClauseRebinding do
  def run(other) do
    renamee = 0

    case other do
      :inc ->
        renamee = renamee + 1
        renamee

      _ ->
        renamee
    end
  end
end
