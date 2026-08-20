defmodule Enum do
  def map(enumerable, fun), do: fun
  def map_size(value), do: value
end

defmodule Usage do
  @doc delegate_to: {Enum, :ma<caret>p, 2}
end
