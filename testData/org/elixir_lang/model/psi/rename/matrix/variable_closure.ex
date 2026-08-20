defmodule ClosureSites do
  def run(renamee) do
    fn -> renamee + 1 end
  end
end
