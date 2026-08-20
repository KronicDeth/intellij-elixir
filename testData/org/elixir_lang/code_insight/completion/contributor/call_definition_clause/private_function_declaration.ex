defmodule Prefix.PrivateFunctionDeclaration do
  def public_function1(), do: :c
  def public_function2(), do: :d
  defp private_function1(), do: :a
  defp private_function2(), do: :b
end
