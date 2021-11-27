defmodule Defdelegate do
  defdelegate source(), to: Source
  defdelegate source_as(), to: Source, as: :source

  def usage do
    s<caret>
  end
end
