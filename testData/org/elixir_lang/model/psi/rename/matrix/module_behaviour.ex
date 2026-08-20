defmodule Renamee do
  @callback hook(term) :: term
end

defmodule BehaviourClient do
  @behaviour Renamee

  @impl true
  def hook(x), do: x
end
