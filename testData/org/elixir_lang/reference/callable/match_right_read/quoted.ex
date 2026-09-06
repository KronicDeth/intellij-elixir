defmodule Quoted do
  defmacro __using__(_) do
    quote do
      injected = 1
    end
  end
end
