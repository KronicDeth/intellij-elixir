defmodule VariableGotoDeclaration do
  def run do
    variable = 1
    IO.puts("#{va<caret>riable}")
  end
end
