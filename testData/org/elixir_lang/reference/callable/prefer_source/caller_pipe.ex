defmodule CallerPipe do
  def example do
    "1 + 2" |> Code.<caret>string_to_quoted()
  end
end
