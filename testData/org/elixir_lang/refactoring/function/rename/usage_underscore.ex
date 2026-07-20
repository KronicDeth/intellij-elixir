defmodule RenameMod do
  def map_it do
    apply(Enum, :map, [[], fn x -> x end])
  end

  def do_it do
    map_<caret>it()
  end
end
