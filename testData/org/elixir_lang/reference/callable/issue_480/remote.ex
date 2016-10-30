defmodule AliasedModuleQualifier do
  import MyNamespace.Referenced

  %{}
  |> <caret>changeset()
end
