defmodule MacroDef do
  defmacro renamee(x) do
    quote do
      unquote(x) + 1
    end
  end
end

defmodule MacroUser do
  import MacroDef

  def run(x) do
    renamee(x)
  end
end
