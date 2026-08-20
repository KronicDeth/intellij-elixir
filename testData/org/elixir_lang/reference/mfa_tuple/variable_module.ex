defmodule Target do
  def run(), do: :ok
end

defmodule Usage do
  mod = Target
  @doc delegate_to: {mod, :ru<caret>n, 0}
end
