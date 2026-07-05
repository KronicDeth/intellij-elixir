defmodule UsagesUseInjectedBehaviour do
  @callback per<caret>form() :: any

  defmacro __using__(_) do
    quote do
      @behaviour UsagesUseInjectedBehaviour
    end
  end
end

defmodule UsagesUseInjectedImpl do
  use UsagesUseInjectedBehaviour

  def perform, do: :ok
end
