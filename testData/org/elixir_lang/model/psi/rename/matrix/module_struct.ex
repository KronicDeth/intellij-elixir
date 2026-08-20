defmodule Renamee do
  defstruct [:field]
end

defmodule StructClient do
  def build do
    %Renamee{field: 1}
  end

  def read(%Renamee{field: f}), do: f
end
