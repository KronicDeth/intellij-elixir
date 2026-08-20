defmodule CallbackDef do
  @callback renamee(term) :: term
end

defmodule CallbackImpl do
  @behaviour CallbackDef

  @impl true
  def renamee(x), do: x
end
