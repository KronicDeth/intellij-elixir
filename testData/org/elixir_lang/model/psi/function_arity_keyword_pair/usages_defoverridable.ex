defmodule UsagesOverridableBehaviour do
  @callback per<caret>form() :: any

  defmacro __using__(_opts) do
    quote do
      @behaviour UsagesOverridableBehaviour

      def perform, do: :default

      defoverridable perform: 0
    end
  end
end
