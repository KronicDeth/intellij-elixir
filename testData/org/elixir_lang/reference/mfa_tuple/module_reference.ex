defmodule Target do
  def map(_enumerable, _fun), do: :ok
end

defmodule Usage do
  @doc delegate_to: {Tar<caret>get, :map, 2}
end
