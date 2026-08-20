defmodule Processor do
  def process([], acc), do: Enum.reverse(acc)
  def process([h | t], acc), do: process(t, [h * 2 | acc])
end

defmodule Usage do
  @doc delegate_to: {Processor, :pro<caret>cess, 2}
end
