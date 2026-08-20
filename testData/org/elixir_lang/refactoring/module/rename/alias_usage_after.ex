defmodule RenamedModule do
  def run, do: :ok
end

defmodule Consumer do
  alias RenamedModule

  def call, do: RenamedModule.run()
end
