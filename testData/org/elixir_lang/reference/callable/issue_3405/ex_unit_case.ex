defmodule ExUnit.Case do
  defmacro __using__(_opts) do
    quote do
      import ExUnit.Case
    end
  end

  defmacro describe(message, do: block) do
    quote do
      unquote(message)
      unquote(block)
    end
  end

  defmacro test(message, do: block) do
    quote do
      unquote(message)
      unquote(block)
    end
  end
end
