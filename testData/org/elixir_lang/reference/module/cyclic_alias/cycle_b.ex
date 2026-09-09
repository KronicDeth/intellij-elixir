defmodule CycleB do
  use CycleA

  defmacro __using__(_opts) do
    quote do
      alias Missing
    end
  end
end
