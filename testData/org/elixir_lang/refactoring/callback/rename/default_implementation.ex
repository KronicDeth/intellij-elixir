defmodule CallbackRenameBehaviour do
  @callback perform() :: any

  defmacro __using__(_opts) do
    quote do
      @behaviour CallbackRenameBehaviour

      def per<caret>form, do: :default

      defoverridable perform: 0
    end
  end
end

defmodule CallbackRenameOverride do
  use CallbackRenameBehaviour

  @impl CallbackRenameBehaviour
  def perform, do: :custom
end
