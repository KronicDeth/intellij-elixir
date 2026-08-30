defmodule Shadowing do
  def run(list) do
    node = 1
    Enum.map(list, fn no<caret>de -> node + 1 end)
    node
  end
end
