defmodule CallbackRenameBehaviour do
  @callback per<caret>form() :: any
end

defmodule CallbackRenameImpl do
  @behaviour CallbackRenameBehaviour

  def perform, do: :ok
end
