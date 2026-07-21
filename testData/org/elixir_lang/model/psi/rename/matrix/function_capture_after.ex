defmodule CaptureSites do
  def fresh(x), do: x * 2

  def run(list) do
    local = Enum.map(list, &fresh/1)
    remote = Enum.map(list, &CaptureSites.fresh/1)
    {local, remote}
  end
end
