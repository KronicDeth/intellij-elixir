defmodule CasePatternInMatchSites do
  def run(v) do
    result = case v do
      {:ok, renamee} -> renamee
    end
    result
  end
end
