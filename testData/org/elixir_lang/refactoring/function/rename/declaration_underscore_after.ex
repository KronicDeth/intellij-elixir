defmodule RenameMod do
  def map_rename_it do
    apply(Enum, :map, [[], fn x -> x end])
  end

  def do_it do
    map_rename_it()
  end
end
