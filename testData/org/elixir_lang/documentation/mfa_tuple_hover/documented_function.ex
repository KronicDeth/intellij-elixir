defmodule Documented do
  @doc """
  Maps a function over an enumerable.

  Returns a new list with the results.
  """
  def map(enumerable, fun), do: Enum.map(enumerable, fun)
end

defmodule Usage do
  @doc delegate_to: {Documented, :ma<caret>p, 2}
  def map(enumerable, fun), do: Documented.map(enumerable, fun)
end
