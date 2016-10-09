defmodule Prefix.MixedDeclaration do
  # Macros

  defmacro public_macro, do: :a

  ## Private Macros

  defmacrop private_macro, do: :b

  # Functions

  def public_function, do: :c

  ## Private Functions

  def private_function, do: :d

  # Modules

  defmodule Nested do
  end
end
