defmodule Fresh do
  @callback hook(term) :: term
end

defmodule BehaviourClient do
  @behaviour Fresh

  @impl true
  def hook(x), do: x
end
