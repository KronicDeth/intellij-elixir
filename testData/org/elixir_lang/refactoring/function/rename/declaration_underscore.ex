defmodule RenameMod do
  def map_<caret>it do
    apply(Enum, :map, [[], fn x -> x end])
  end

  def do_it do
    map_it()
  end
end
