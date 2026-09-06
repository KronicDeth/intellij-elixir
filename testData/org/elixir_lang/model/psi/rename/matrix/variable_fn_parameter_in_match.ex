defmodule FnParameterInMatchSites do
  def run do
    f = fn renamee -> renamee end
    f
  end
end
