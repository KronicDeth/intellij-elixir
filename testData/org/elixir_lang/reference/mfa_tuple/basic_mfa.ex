defmodule Target do
  def map(enumerable, fun), do: Enum.map(enumerable, fun)
end

defmodule Usage do
  @doc delegate_to: {Target, :ma<caret>p, 2}
end
