defmodule MultiClause do
  def fresh(0), do: 1
  def fresh(n), do: n * fresh(n - 1)
end
