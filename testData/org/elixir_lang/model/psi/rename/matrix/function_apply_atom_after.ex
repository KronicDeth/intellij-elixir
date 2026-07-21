defmodule ApplySites do
  def fresh(x), do: x

  def run(x) do
    apply(ApplySites, :fresh, [x])
  end
end
