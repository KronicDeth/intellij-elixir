defprotocol UsagesNonImplementingProtocol do
  def per<caret>form(data)
end

defmodule UsagesNonImplementingModule do
  def perform(data), do: :ok
  def run(value), do: OtherModule.perform(value)
end
