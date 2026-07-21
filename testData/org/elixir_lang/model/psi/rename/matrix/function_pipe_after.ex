defmodule PipeSites do
  def fresh(x), do: x * 2

  def run(x) do
    x |> fresh() |> PipeSites.fresh()
  end
end
