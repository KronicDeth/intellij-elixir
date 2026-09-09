defmodule Prefix.StructFieldExistingCallUsage do
  alias Prefix.PublicFunctionDeclaration

  def run(n) do
    %SomeStruct{name: PublicFunctionDeclaration.<caret>public_function1(n)}
  end
end
