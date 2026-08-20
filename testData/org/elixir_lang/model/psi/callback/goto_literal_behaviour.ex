defmodule GotoLiteralBehaviour do
  @callback perform() :: any
end

defmodule GotoLiteralImpl do
  @behaviour GotoLiteralBehaviour

  def per<caret>form, do: :ok
end
