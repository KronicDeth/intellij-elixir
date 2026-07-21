defmodule ArityFamilies do
  @spec fresh(integer) :: integer
  def fresh(x), do: x + 1

  def renamee(x, y), do: x + y

  def one(x) do
    fresh(x)
  end

  def two(x) do
    renamee(x, x)
  end
end
