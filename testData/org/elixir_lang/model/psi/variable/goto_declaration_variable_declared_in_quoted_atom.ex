defmodule VariableGotoDeclaration do
  def run do
    _ = :"a#{variable = 1}"
    va<caret>riable
  end
end
