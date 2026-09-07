defmodule Prefix.UseNoArgumentsDoBlockUsage do
  alias Prefix.PublicFunctionDeclaration

  use do
    PublicFunctionDeclaration.<caret>
  end
end
