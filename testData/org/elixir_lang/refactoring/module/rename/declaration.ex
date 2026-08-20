defmodule MyMod<caret>ule do
  def run, do: :ok
end

defmodule Consumer do
  def call do
    MyModule.run()
  end
end
