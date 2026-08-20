defmodule AtomRename do
  def per<caret>form, do: :ok

  def run do
    apply(AtomRename, :perform, [])
  end
end
