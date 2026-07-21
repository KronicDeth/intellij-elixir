defmodule MultiClause do
  def renamee(0), do: 1
  def renamee(n), do: n * renamee(n - 1)
end
