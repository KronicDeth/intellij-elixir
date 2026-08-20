defmodule UsagesLiteralBehaviour do
  @callback per<caret>form() :: any
end

defmodule UsagesLiteralImpl do
  @behaviour UsagesLiteralBehaviour

  def perform, do: :ok
end
