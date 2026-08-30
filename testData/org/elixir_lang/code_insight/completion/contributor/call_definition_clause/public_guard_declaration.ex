defmodule Prefix.PublicGuardDeclaration do
  defguard public_guard1(value) when is_integer(value)
  defguard public_guard2(value) when is_atom(value)
end
