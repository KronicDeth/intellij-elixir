defprotocol UsagesLiteralProtocol do
  def per<caret>form(data)
end

defimpl UsagesLiteralProtocol, for: Atom do
  def perform(data), do: :ok
end

defmodule UsagesLiteralCaller do
  def run(value), do: UsagesLiteralProtocol.perform(value)
end
