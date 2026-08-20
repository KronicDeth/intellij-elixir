defmodule RenameMod do
  def map_it do
    :ok
  end

  def do_it do
    &map_<caret>it/0
  end
end
