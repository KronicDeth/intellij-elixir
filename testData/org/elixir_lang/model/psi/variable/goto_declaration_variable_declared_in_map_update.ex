defmodule VariableGotoDeclaration do
  def run(m) do
    _ = %{m | k: variable = 1}
    va<caret>riable
  end
end
