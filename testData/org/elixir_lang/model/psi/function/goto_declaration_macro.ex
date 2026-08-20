defmodule GotoDeclaration do
  defmacro perform(data) do
    quote do: unquote(data)
  end

  def run(value), do: per<caret>form(value)
end
