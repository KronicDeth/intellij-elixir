defmodule WithSites do
  def run(map) do
    with {:ok, renamee} <- Map.fetch(map, :key) do
      renamee
    end
  end
end
