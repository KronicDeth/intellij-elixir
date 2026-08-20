defprotocol UsagesUseInjectedProtocol do
  def per<caret>form(data)
end

defimpl UsagesUseInjectedProtocol, for: Atom do
  def perform(data), do: :ok
end

defmodule UsagesUseInjectedCaller do
  def run(value), do: UsagesUseInjectedProtocol.perform(value)
end
