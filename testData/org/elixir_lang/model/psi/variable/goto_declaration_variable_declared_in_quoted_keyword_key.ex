defmodule VariableGotoDeclaration do
  def run do
    _ = ["#{variable = 1}": 0]
    va<caret>riable
  end
end
