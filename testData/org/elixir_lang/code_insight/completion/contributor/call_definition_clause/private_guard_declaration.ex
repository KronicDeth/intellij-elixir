defmodule Prefix.PrivateGuardDeclaration do
  defguard public_guard1(value) when is_integer(value)
  defguard public_guard2(value) when is_atom(value)
  defguardp private_guard1(value) when is_binary(value)
  defguardp private_guard2(value) when is_list(value)
end
