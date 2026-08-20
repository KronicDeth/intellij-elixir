defmodule ErlangQualifiedUsage do
  def sqrt(x), do: :erlang.sqrt(x)
end

defmodule ErlangQualifiedTarget do
  def sq<caret>rt(x), do: x
end
