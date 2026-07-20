defmodule VariableRebindingAndPin do
  def run(parameter) do
    value = par<caret>ameter
    ^value = value
    value = value + 1
    value
  end
end
