defmodule RenamedModule do
  def run, do: :ok
end

defmodule Consumer do
  import RenamedModule

  def call, do: run()
end
