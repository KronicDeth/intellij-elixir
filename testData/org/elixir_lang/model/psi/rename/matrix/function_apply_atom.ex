defmodule ApplySites do
  def renamee(x), do: x

  def run(x) do
    apply(ApplySites, :renamee, [x])
  end
end
