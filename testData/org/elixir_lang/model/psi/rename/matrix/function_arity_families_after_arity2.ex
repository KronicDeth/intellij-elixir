defmodule ArityFamilies do
  @spec renamee(integer) :: integer
  def renamee(x), do: x + 1

  def fresh(x, y), do: x + y

  def one(x) do
    renamee(x)
  end

  def two(x) do
    fresh(x, x)
  end
end
