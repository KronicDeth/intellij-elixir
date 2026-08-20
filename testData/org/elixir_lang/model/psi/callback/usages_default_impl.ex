defmodule UsagesDefaultBehaviour do
  @callback per<caret>form() :: any

  defmacro __using__(_) do
    quote do
      @behaviour UsagesDefaultBehaviour

      def perform, do: :default
    end
  end
end
