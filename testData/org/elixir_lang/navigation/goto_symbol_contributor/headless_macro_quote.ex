defmodule HeadlessMacroQuote do
  defmacro do
    quote do
      def foo, do: :ok
    end
  end
end
