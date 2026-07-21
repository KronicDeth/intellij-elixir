defmodule MacroCallbackDef do
  @macrocallback fresh(term) :: Macro.t()
end

defmodule MacroCallbackImpl do
  @behaviour MacroCallbackDef

  @impl true
  defmacro fresh(x), do: x
end
