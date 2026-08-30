defmodule MapPatternSites do
  defp same_id(%{id: fresh}, %{id: fresh}), do: :ok
end
