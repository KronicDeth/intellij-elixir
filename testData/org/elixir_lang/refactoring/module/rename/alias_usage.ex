defmodule MyModule do
  def run, do: :ok
end

defmodule Consumer do
  alias MyMod<caret>ule

  def call, do: MyModule.run()
end
