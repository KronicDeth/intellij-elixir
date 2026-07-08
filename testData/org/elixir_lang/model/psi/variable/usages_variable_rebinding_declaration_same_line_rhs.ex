defmodule VariableRebindingAndPin do
  def run(parameter) do
    value = parameter
    ^value = value
    va<caret>lue = value + 1
    value
  end
end
