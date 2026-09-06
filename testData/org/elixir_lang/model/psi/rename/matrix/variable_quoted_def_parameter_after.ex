defmodule QuotedDefSites do
  defmacro inject do
    quoted =
      quote do
        def greet(fresh), do: fresh
      end

    quoted
  end
end
