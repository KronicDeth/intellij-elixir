defmodule ComprehensionSites do
  def run(list) do
    for renamee <- list, do: renamee * 2
  end
end
