defmodule SpecWhenBoundVariable do
  @spec identity(a) :: a when a: term()
  def identity(value), do: value
end
