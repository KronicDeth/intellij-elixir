defmodule MyModule do
  defmacro __using__(_opts), do: :ok
end

defmodule Consumer do
  use MyMod<caret>ule
end
