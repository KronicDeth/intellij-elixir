defmodule CallbackDef do
  @callback fresh(term) :: term
end

defmodule CallbackImpl do
  @behaviour CallbackDef

  @impl true
  def fresh(x), do: x
end
