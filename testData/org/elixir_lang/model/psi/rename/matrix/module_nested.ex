defmodule Outer.Renamee do
  def hello, do: :world
end

defmodule NestedClient do
  alias Outer.Renamee

  def call do
    Renamee.hello()
  end
end
