defmodule AliasedModuleQualifier do
  alias MyNamespace.Referenced
  alias Referenced, as: Refd

  Refd.<caret>changeset(%{})
end
