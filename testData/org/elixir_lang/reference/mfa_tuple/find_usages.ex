defmodule Mapper do
  def map(enumerable, fun), do: Enum.map(enumerable, fun)
end

defmodule Usage do
  @doc delegate_to: {Mapper, :ma<caret>p, 2}
  def map(enumerable, fun), do: Mapper.map(enumerable, fun)
end
