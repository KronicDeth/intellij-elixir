defmodule Prefix.UseOptionValueUsage do
  alias Prefix.PublicFunctionDeclaration

  use Prefix.SomeBehaviour, adapter: PublicFunctionDeclaration.<caret>
end
