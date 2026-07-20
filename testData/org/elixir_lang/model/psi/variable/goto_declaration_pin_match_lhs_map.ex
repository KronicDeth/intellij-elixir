defmodule VariableRebindingAndPin do
    def run(parameter) do
    value = parameter
    %{ item: ^v<caret>alue} = %{ item: value }
    value = value + 1
    value
  end
end
