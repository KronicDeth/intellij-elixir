defprotocol GotoSuperProtocol do
  def run(value)
end

defimpl GotoSuperProtocol, for: Atom do
  def ru<caret>n(value), do: value
end
