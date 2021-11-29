defmodule MyException do
  defexception [:message]

  def extra do
    e<caret>
  end
end
