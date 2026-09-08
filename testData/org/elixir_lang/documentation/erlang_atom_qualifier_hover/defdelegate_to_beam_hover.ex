defmodule Delegator do
  @moduledoc "Delegator module."
  defdelegate new(), to: :queue
end

defmodule Caller do
  def run do
    Delegator.n<caret>ew()
  end
end
