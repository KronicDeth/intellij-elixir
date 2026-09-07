defmodule FnParameterInMatchSites do
  def run do
    f = fn fresh -> fresh end
    f
  end
end
