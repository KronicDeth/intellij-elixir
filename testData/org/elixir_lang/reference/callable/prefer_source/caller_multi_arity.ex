defmodule CallerMultiArity do
  def example do
    Code.<caret>string_to_quoted("1 + 2", [])
  end
end
