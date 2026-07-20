defmodule CallbackRenameBehaviour do
  @callback perform() :: any

  defmacro __using__(_opts) do
    quote do
      @behaviour CallbackRenameBehaviour

      def perform, do: :default

      defoverridable perform: 0
    end
  end
end

defmodule CallbackRenameOverride do
  use CallbackRenameBehaviour

  @impl CallbackRenameBehaviour
  def per<caret>form, do: :custom
end
