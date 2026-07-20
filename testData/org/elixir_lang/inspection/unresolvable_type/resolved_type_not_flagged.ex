defmodule UsesExistingType do
  @spec convert(Other.existing()) :: :ok
  def convert(_value), do: :ok
end
