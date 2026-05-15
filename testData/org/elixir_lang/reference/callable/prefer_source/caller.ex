defmodule Caller do
  def call_string_to_quoted do
    Code.<caret>string_to_quoted("1 + 2")
  end
end
