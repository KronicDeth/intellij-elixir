defmodule MatchRhsReadSites do
  def run(input) do
    fresh = input
    list = [fresh]
    string = "#{fresh}"
    copy = fresh
    {list, string, copy}
  end
end
