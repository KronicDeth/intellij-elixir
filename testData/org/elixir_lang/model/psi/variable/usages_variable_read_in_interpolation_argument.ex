defmodule VariableUsages do
  def run do
    va<caret>riable = 1
    IO.puts("#{variable}")
  end
end
