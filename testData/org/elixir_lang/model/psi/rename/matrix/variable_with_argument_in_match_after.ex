defmodule WithInMatchSites do
  def run(fresh) do
    result = with {:ok, v} <- fetch(fresh), do: v
    result
  end
end
