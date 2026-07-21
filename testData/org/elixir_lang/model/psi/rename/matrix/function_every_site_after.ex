defmodule FunSites do
  @spec fresh(integer) :: integer
  def fresh(x), do: x + 1

  def caller(x) do
    fresh(x)
  end
end

defmodule FunSitesClient do
  def call(x) do
    FunSites.fresh(x)
  end
end
