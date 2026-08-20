defmodule VariableRebindingAndPin do
  def run(parameter) do
    value = parameter
    [^value | _tail] = [v<caret>alue]
    value = value + 1
    value
  end
end
