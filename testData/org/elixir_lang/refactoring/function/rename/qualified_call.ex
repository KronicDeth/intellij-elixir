defmodule RenameMod do
  def map_it do
    :ok
  end
end

defmodule Consumer do
  def run do
    RenameMod.map<caret>_it()
  end
end
