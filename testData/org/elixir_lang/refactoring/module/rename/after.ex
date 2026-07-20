defmodule RenamedModule do
  def run, do: :ok
end

defmodule Consumer do
  def call do
    RenamedModule.run()
  end
end
