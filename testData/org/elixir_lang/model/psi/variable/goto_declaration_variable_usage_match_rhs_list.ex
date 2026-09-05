defmodule VariableGotoDeclaration do
  def run do
    variable = 1
    y = [va<caret>riable]
    y
  end
end
