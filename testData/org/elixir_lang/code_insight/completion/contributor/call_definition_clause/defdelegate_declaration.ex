defmodule Prefix.DefdelegateDeclaration do
  def merge(map1, map2, fun), do: {map1, map2, fun}
  defdelegate merge(map1, map2), to: :maps
  defdelegate values(map), to: :maps
end
