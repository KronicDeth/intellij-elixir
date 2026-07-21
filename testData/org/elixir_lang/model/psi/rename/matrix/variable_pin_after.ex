defmodule PinSites do
  def run(fresh, actual) do
    case actual do
      ^fresh -> :match
      _ -> :mismatch
    end
  end
end
