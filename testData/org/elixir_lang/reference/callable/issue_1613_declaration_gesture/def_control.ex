defmodule Delegator do
  def own_<caret>merge(m1, m2), do: {m1, m2}

  def run do
    own_merge(%{}, %{})
  end
end
