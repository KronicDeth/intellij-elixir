defmodule ErlangQualifiedGotoDeclaration do
  def sqrt(value), do: value

  def run(value), do: :erlang.sq<caret>rt(value)
end
