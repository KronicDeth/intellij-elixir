defmodule Behaviour do
  @callback function_callback() :: term()
  @macrocallback macro_callback(term()) :: term()

  def helper do
    call<caret>
  end
end
