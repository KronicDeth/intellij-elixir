defmodule Prefix.ExistingCallUsage do
  alias Prefix.PublicFunctionDeclaration

  def run(n) do
    PublicFunctionDeclaration.<caret>public_function1(n)
  end
end
