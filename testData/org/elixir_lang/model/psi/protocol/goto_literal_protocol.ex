defprotocol GotoLiteralProtocol do
  def per<caret>form()
end

defimpl GotoLiteralProtocol, for: Atom do
  def perform, do: :ok
end
