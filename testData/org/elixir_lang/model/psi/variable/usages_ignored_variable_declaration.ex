defmodule IgnoredVariableUsages do
  def run do
    _va<caret>lue = 1
    :ok
  end
end
