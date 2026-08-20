defmodule MacroCallbackDef do
  @macrocallback renamee(term) :: Macro.t()
end

defmodule MacroCallbackImpl do
  @behaviour MacroCallbackDef

  @impl true
  defmacro renamee(x), do: x
end
