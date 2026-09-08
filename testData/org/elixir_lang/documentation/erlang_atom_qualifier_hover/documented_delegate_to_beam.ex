defmodule Delegator do
  @moduledoc "Delegator module."
  @doc "Delegator's own account of queueing."
  defdelegate new(), to: :queue
end

defmodule Caller do
  def run do
    Delegator.n<caret>ew()
  end
end
