defmodule PipeSites do
  def renamee(x), do: x * 2

  def run(x) do
    x |> renamee() |> PipeSites.renamee()
  end
end
