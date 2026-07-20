defmodule CallbackRenameBehaviour do
  @callback perform() :: any
end

defmodule CallbackRenameImpl do
  @behaviour CallbackRenameBehaviour

  def per<caret>form, do: :ok
end
