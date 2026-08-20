defmodule GuardSites do
  defguard renamee(n) when rem(n, 2) == 0

  def check(n) when renamee(n), do: :even
end
