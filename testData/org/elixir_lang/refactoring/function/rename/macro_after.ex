defmodule RenameMod do
  defmacro renamed_it(clause, do: expression) do
    quote do
      if !unquote(clause), do: unquote(expression)
    end
  end

  def do_it do
    renamed_it(false, do: :ok)
  end
end
