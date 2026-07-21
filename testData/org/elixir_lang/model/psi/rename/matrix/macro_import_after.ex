defmodule MacroDef do
  defmacro fresh(x) do
    quote do
      unquote(x) + 1
    end
  end
end

defmodule MacroUser do
  import MacroDef

  def run(x) do
    fresh(x)
  end
end
