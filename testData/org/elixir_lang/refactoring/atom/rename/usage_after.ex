defmodule AtomRename do
  def execute, do: :ok

  def run do
    apply(AtomRename, :execute, [])
  end
end
