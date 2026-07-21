defmodule FunSites do
  @spec renamee(integer) :: integer
  def renamee(x), do: x + 1

  def caller(x) do
    renamee(x)
  end
end

defmodule FunSitesClient do
  def call(x) do
    FunSites.renamee(x)
  end
end
