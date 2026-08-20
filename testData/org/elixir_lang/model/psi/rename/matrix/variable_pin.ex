defmodule PinSites do
  def run(renamee, actual) do
    case actual do
      ^renamee -> :match
      _ -> :mismatch
    end
  end
end
