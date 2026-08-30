defmodule Shadowing do
  def run(list) do
    no<caret>de = 1
    Enum.map(list, fn node -> node + 1 end)
    node
  end
end
