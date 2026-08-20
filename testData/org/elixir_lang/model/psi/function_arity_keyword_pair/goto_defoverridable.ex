defmodule GotoOverridableBehaviour do
  @callback perform() :: any

  defmacro __using__(_opts) do
    quote do
      @behaviour GotoOverridableBehaviour

      def perform, do: :default

      defoverridable per<caret>form: 0
    end
  end
end
