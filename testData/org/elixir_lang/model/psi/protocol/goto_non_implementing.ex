defprotocol GotoNonImplementingProtocol do
  def per<caret>form()
end

defimpl GotoNonImplementingProtocol, for: Atom do
  def other_function, do: :ok
end
