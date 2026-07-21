defmodule MfaSites do
  def renamee(x), do: x

  def run(x) do
    m = {MfaSites, :renamee, 1}
    call = renamee(x)
    {m, call}
  end
end
