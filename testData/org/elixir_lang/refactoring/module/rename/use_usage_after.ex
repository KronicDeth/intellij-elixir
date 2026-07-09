defmodule RenamedModule do
  defmacro __using__(_opts), do: :ok
end

defmodule Consumer do
  use RenamedModule
end
