defmodule GotoUseInjectedBehaviour do
  @callback perform() :: any

  defmacro __using__(_) do
    quote do
      @behaviour GotoUseInjectedBehaviour
    end
  end
end

defmodule GotoUseInjectedImpl do
  use GotoUseInjectedBehaviour

  def per<caret>form, do: :ok
end
