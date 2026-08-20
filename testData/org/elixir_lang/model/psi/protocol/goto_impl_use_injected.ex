defprotocol GotoImplUseInjectedProtocol do
  def perform()
end

defimpl GotoImplUseInjectedProtocol, for: Atom do
  def per<caret>form, do: :ok
end
