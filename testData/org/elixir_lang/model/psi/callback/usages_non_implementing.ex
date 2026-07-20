defmodule UsagesNonImplementingBehaviour do
  @callback per<caret>form() :: any
end

defmodule UsagesUnrelated do
  def perform, do: :ok
end
