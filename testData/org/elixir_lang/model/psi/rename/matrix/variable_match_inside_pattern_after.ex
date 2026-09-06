defmodule MatchInsidePatternSites do
  def run(map) do
    %{a: [_ | _] = fresh} = map
    fresh
  end
end
