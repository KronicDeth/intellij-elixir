defmodule OverridableDef do
  @callback renamee(term) :: term

  defmacro __using__(_) do
    quote do
      @behaviour OverridableDef

      def renamee(x), do: x

      defoverridable renamee: 1
    end
  end
end
