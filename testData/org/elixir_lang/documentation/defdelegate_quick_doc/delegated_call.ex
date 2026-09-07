defmodule DelegateTarget do
  @moduledoc "DelegateTarget module."
  @doc "Merges two maps."
  def merge(map1, map2) do
    Map.merge(map1, map2)
  end
end

defmodule Delegator do
  @moduledoc "Delegator module."
  defdelegate merge(map1, map2), to: DelegateTarget
end

defmodule Caller do
  def run do
    Delegator.mer<caret>ge(%{}, %{})
  end
end
