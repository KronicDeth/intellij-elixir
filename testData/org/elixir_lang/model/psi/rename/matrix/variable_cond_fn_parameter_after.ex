defmodule CondFnSites do
  def run(c) do
    handler =
      cond do
        c -> fn fresh -> fresh + 1 end
        true -> nil
      end

    handler
  end
end
