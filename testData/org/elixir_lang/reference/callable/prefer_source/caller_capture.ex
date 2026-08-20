defmodule CallerCapture do
  def example do
    fun = &Code.<caret>string_to_quoted/1
    fun.("1 + 2")
  end
end
