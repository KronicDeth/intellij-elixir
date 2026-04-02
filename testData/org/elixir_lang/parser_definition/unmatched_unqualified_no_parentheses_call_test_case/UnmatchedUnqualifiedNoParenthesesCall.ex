defmacro defmodule_with_length(name, do: block) do
  length = length(Atom.to_charlist(name))
  quote do
    defmodule unquote(name) do
      def name_length, do: unquote(length)
      unquote(block)
    end
  end
end
