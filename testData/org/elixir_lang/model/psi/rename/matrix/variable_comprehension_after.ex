defmodule ComprehensionSites do
  def run(list) do
    for fresh <- list, do: fresh * 2
  end
end
