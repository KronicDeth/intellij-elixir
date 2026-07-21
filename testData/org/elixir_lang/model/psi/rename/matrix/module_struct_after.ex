defmodule Fresh do
  defstruct [:field]
end

defmodule StructClient do
  def build do
    %Fresh{field: 1}
  end

  def read(%Fresh{field: f}), do: f
end
