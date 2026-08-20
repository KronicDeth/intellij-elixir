defmodule VariableRebindingAndPin do
    def run(parameter) do
    value = parameter
    %{ item: ^value} = %{ item: va<caret>lue }
    value = value + 1
    value
  end
end
