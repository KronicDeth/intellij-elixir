defmodule Foo do
  def bar do
    total = 1
    tally = 3

    run = fn ->
      total = 2
      t<caret>
    end

    run
  end
end
