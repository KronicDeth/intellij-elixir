defmodule ChainedCallQualifierTest do
  alias MyNamespace.Referenced

  def test do
    Referenced.changeset(%{}).name
  end
end
