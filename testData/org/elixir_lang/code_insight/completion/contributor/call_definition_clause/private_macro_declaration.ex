defmodule Prefix.PrivateMacroDeclaration do
  defmacro public_macro1(), do: :c
  defmacro public_macro2(), do: :d
  defmacrop private_macro1(), do: :a
  defmacrop private_macro2(), do: :b
end
