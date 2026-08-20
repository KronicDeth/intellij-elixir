defmodule MyModule do
  def run, do: :ok
end

defmodule Consumer do
  import MyMod<caret>ule

  def call, do: run()
end
