defmodule RenameMod do
  def han<caret>dle(:a), do: 1
  def handle(:b), do: 2

  def call_it do
    handle(:a) + handle(:b)
  end
end
