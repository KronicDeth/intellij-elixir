defmodule VariableRebindingAndPin do
  def run(parameter) do
    value = parameter
    {^value, :ok} = {v<caret>alue, :ok}
    value = value + 1
    value
  end
end
