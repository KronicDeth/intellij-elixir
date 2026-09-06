defmodule VariableUsages do
  def run do
    _ = "#{va<caret>riable = 1}"
    variable
  end
end
