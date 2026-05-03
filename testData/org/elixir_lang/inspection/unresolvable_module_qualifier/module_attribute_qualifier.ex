defmodule ModuleAttributeQualifierTest do
  @mod MyModule

  def test do
    @mod.changeset(%{})
  end
end
