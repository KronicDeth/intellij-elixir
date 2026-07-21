defmodule GuardSites do
  defguard fresh(n) when rem(n, 2) == 0

  def check(n) when fresh(n), do: :even
end
