defmodule MixCompiledTest do
  use ExUnit.Case
  doctest MixCompiled

  test "greets the world" do
    assert MixCompiled.hello() == :world
  end
end
