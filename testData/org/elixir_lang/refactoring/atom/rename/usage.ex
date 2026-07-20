defmodule AtomRename do
  def perform, do: :ok

  def run do
    apply(AtomRename, :per<caret>form, [])
  end
end
