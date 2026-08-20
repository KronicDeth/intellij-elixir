defmodule VariableRebindingAndPin do
  def run(parameter) do
    value = parameter
    {^v<caret>alue, :ok} = {value, :ok}
    value = value + 1
    value
  end
end
