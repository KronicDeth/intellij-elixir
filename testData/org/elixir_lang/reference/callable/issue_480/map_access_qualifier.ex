defmodule AliasedModuleQualifier do
  referenced = %MyNamespace.Referenced{}

  %{}
  |> referenced.__struct__.<caret>__struct__()
end
