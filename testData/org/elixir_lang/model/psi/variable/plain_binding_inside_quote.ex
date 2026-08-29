defmodule PlainBindingInsideQuote do
  defmacro assign_and_read do
    quote do
      bar = 1
      ba<caret>r
    end
  end
end
