defmodule CurrentModuleQualifierTest do
  def changeset(value) do
    value
  end

  def test do
    __MODULE__.changeset(%{})
  end
end
