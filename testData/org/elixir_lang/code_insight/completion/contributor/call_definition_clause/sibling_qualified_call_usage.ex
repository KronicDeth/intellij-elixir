defmodule Prefix.SiblingQualifiedCallUsage do
  alias Prefix.PublicFunctionDeclaration

  def run do
    PublicFunctionDeclaration.<caret>
    PublicFunctionDeclaration.public_function1()
  end
end
