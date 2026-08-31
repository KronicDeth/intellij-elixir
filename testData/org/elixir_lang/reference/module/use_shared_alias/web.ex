defmodule SharedWeb do
  defmacro __using__(_opts) do
    quote do
      alias Shared.Query
    end
  end
end
