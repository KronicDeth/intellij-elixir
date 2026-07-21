defmodule Outer.Fresh do
  def hello, do: :world
end

defmodule NestedClient do
  alias Outer.Fresh

  def call do
    Fresh.hello()
  end
end
