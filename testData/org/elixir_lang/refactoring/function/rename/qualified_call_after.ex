defmodule RenameMod do
  def map_rename_it do
    :ok
  end
end

defmodule Consumer do
  def run do
    RenameMod.map_rename_it()
  end
end
