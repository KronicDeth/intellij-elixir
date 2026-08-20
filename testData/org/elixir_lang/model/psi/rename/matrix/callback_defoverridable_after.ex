defmodule OverridableDef do
  @callback fresh(term) :: term

  defmacro __using__(_) do
    quote do
      @behaviour OverridableDef

      def fresh(x), do: x

      defoverridable fresh: 1
    end
  end
end
