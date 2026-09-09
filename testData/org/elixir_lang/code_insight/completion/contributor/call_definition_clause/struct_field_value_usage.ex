defmodule Prefix.StructFieldValueUsage do
  alias Prefix.PublicFunctionDeclaration

  def run do
    %SomeStruct{name: PublicFunctionDeclaration.<caret>}
  end
end
