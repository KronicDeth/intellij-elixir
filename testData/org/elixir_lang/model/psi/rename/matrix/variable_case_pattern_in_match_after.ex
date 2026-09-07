defmodule CasePatternInMatchSites do
  def run(v) do
    result = case v do
      {:ok, fresh} -> fresh
    end
    result
  end
end
