defmodule VariableRebindingAndPin do
  def run(parameter) do
    value = parameter
    [^v<caret>alue | _tail] = [value]
    value = value + 1
    value
  end
end
