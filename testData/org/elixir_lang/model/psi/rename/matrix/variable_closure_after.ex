defmodule ClosureSites do
  def run(fresh) do
    fn -> fresh + 1 end
  end
end
