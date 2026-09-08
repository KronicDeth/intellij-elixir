defmodule DelegateTarget do
  @moduledoc "DelegateTarget module."
  def merge(map1, map2), do: {map1, map2}
  def values(map), do: map
end

defmodule Delegator do
  @moduledoc "Delegator module."

  @doc "Delegator's own account of merging."
  defdelegate documented(m1, m2), to: DelegateTarget, as: :merge

  defdelegate undocumented(x), to: DelegateTarget, as: :values

  def plain(x), do: x
end

defmodule Caller do
  def run do
    Delegator.undocu<caret>mented(1)
  end
end
