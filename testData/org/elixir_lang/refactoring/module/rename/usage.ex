defmodule MyModule do
  def run, do: :ok
end

defmodule Consumer do
  def call do
    MyMod<caret>ule.run()
  end
end
