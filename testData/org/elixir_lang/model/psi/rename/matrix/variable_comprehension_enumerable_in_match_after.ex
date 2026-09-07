defmodule ComprehensionInMatchSites do
  def run(fresh) do
    result = for item <- fresh, do: item
    result
  end
end
