defmodule MfaSites do
  def fresh(x), do: x

  def run(x) do
    m = {MfaSites, :fresh, 1}
    call = fresh(x)
    {m, call}
  end
end
