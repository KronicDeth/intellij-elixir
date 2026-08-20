defmodule QuotedUnquoteQualifierTest do
  def test(mod) do
    quote do
      unquote(mod).changeset(%{})
    end
  end
end
