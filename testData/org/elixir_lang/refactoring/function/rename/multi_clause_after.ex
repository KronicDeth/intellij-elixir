defmodule RenameMod do
  def process(:a), do: 1
  def process(:b), do: 2

  def call_it do
    process(:a) + process(:b)
  end
end
