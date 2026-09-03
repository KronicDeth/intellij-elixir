defmodule MapPatternSites do
  defp same_id(%{id: renamee}, %{id: renamee}), do: :ok
end
