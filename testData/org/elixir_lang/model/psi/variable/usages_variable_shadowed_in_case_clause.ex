defmodule Shadowing do
  def run(list) do
    no<caret>de = 1

    case list do
      node -> node + 1
    end

    node
  end
end
