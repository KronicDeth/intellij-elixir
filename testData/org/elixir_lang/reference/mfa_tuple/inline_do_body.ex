defmodule Target do
  def map(enumerable, fun), do: Enum.map(enumerable, fun)
end

defmodule Usage do
  def my_mfa, do: {Target, :ma<caret>p, 2}
end
