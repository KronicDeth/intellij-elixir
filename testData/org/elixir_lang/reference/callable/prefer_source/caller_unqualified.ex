defmodule CallerUnqualified do
  import Code

  def call_string_to_quoted do
    <caret>string_to_quoted("1 + 2")
  end
end
