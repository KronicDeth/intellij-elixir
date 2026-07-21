defmodule CaptureSites do
  def renamee(x), do: x * 2

  def run(list) do
    local = Enum.map(list, &renamee/1)
    remote = Enum.map(list, &CaptureSites.renamee/1)
    {local, remote}
  end
end
