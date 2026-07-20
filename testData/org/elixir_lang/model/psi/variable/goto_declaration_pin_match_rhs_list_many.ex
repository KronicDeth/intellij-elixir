defmodule VariableRebindingAndPin do
    def run(parameter) do
    value = parameter
    [^value, value] = [ value, va<caret>lue ]
    value = value + 1
    value
  end
end
