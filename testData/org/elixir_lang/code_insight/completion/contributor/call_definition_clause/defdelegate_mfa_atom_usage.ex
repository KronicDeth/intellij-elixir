defmodule Prefix.DefdelegateMfaAtomUsage do
  alias Prefix.DefdelegateDeclaration

  def call_it(argument) do
    apply(DefdelegateDeclaration, :<caret>, [argument])
  end
end
