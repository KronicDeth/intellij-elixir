defmodule MatchRhsReadSites do
  def run(input) do
    renamee = input
    list = [renamee]
    string = "#{renamee}"
    copy = renamee
    {list, string, copy}
  end
end
