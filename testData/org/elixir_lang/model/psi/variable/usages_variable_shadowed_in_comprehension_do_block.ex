defmodule Shadowing do
  def run(list) do
    no<caret>de = 1

    for node <- list do
      node + 1
    end

    node
  end
end
