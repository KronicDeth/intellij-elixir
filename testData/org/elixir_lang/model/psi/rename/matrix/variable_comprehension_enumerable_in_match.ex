defmodule ComprehensionInMatchSites do
  def run(renamee) do
    result = for item <- renamee, do: item
    result
  end
end
