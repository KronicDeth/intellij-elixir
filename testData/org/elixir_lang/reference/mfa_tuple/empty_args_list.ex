defmodule Worker do
  def run(), do: :ok
end

defmodule Usage do
  @child_spec %{start: {Worker, :ru<caret>n, []}}
end
