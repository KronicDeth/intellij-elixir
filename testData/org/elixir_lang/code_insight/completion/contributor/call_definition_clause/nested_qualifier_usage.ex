defmodule Prefix.NestedQualifierUsage do
  def run do
    Prefix.PublicFunctionDeclaration.<caret>
    Prefix.PublicFunctionDeclaration.public_function1()
  end
end
