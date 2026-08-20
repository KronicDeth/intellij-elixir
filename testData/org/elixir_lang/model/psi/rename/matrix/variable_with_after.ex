defmodule WithSites do
  def run(map) do
    with {:ok, fresh} <- Map.fetch(map, :key) do
      fresh
    end
  end
end
