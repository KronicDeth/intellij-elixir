defmodule Prefix.Reference do
  alias Prefix.{MultipleAliasAye, MultipleAliasBee}

  MultipleAliasAye.Nested<caret>

  @a 1
end
