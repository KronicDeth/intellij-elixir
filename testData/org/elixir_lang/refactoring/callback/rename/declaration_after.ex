defmodule CallbackRenameBehaviour do
  @callback execute() :: any
end

defmodule CallbackRenameImpl do
  @behaviour CallbackRenameBehaviour

  def execute, do: :ok
end
