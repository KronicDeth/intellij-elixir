defmodule AliasedModuleQualifier do
  alias MyNamespace.Referenced

  %{}
  |> Referenced.<caret>changeset()
end
