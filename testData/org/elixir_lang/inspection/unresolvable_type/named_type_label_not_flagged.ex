defmodule NamedTypeLabel do
  @spec start_link(integer(), integer()) :: {:ok, pid()} | {:error, reason :: term()}
  def start_link(_first, _second), do: {:ok, self()}
end
