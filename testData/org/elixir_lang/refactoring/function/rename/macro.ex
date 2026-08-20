defmodule RenameMod do
  defmacro unless<caret>_it(clause, do: expression) do
    quote do
      if !unquote(clause), do: unquote(expression)
    end
  end

  def do_it do
    unless_it(false, do: :ok)
  end
end
