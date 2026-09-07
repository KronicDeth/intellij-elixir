defmodule Prefix.AtomCallFollowsUsage do
  alias Prefix.PublicFunctionDeclaration

  def run do
    PublicFunctionDeclaration.<caret>
    :maps.values(%{})
  end
end
