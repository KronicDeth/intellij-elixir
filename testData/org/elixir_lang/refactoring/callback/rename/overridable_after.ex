defmodule CallbackRenameBehaviour do
  @callback execute() :: any

  defmacro __using__(_opts) do
    quote do
      @behaviour CallbackRenameBehaviour

      def execute, do: :default

      defoverridable execute: 0
    end
  end
end

defmodule CallbackRenameOverride do
  use CallbackRenameBehaviour

  @impl CallbackRenameBehaviour
  def execute, do: :custom
end
