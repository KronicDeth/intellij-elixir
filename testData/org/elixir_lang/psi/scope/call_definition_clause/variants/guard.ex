defmodule GuardVariants do
  defguard is_even_number(value) when rem(value, 2) == 0

  defguardp is_even_tuple(value) when rem(tuple_size(value), 2) == 0

  def check(value) when is_even_<caret>(value), do: :even
end
