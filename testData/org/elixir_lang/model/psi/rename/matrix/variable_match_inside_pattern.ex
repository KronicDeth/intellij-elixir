defmodule MatchInsidePatternSites do
  def run(map) do
    %{a: [_ | _] = renamee} = map
    renamee
  end
end
