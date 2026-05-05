defmodule CallerImportOnly do
  import Code, only: [string_to_quoted: 1]

  def example do
    <caret>string_to_quoted("1 + 2")
  end
end
