defmodule DelegateTarget do
  @moduledoc "DelegateTarget module."
  @doc "Merges two maps."
  def merge(map1, map2) do
    Map.merge(map1, map2)
  end
end

defmodule Delegator do
  @moduledoc "Delegator module."
  defdelegate mer<caret>ge(map1, map2), to: DelegateTarget
end
