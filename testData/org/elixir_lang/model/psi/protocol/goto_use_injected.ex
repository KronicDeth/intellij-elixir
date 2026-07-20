defprotocol GotoUseInjectedProtocol do
  def per<caret>form()
end

defimpl GotoUseInjectedProtocol, for: Atom do
  def perform, do: :ok
end
