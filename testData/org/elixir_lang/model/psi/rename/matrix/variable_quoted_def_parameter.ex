defmodule QuotedDefSites do
  defmacro inject do
    quoted =
      quote do
        def greet(renamee), do: renamee
      end

    quoted
  end
end
