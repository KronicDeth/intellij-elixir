defmodule VariableRebindingAndPin do
  def run(parameter) do
    va<caret>lue = parameter
    ^value = value
    value = value + 1
    value
  end
end
