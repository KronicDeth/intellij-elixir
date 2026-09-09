defmodule CycleA do
  use CycleB

  defmacro __using__(_opts) do
    quote do
      alias Missing
    end
  end
end
