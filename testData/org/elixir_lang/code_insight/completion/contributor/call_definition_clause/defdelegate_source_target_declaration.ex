defmodule Prefix.DefdelegateSourceTarget do
  def values(map), do: map
end

defmodule Prefix.DefdelegateSourceTargetDeclaration do
  defdelegate values(map), to: Prefix.DefdelegateSourceTarget
end
