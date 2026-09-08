defmodule Delegator do
  defdelegate delegated(x), to: NoSuchModuleAnywhere
end

defmodule Caller do
  def run do
    Delegator.dele<caret>gated(1)
  end
end
