defmodule Prefix.UseDoBlockUsage do
  alias Prefix.PublicFunctionDeclaration

  use Prefix.SomeBehaviour do
    PublicFunctionDeclaration.<caret>
  end
end
