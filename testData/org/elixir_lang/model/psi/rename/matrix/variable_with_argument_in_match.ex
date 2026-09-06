defmodule WithInMatchSites do
  def run(renamee) do
    result = with {:ok, v} <- fetch(renamee), do: v
    result
  end
end
