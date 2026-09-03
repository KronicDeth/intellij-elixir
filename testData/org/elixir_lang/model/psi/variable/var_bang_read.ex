defmodule VarBangRead do
  defmacro read_it do
    quote do
      x = var!(fo<caret>o)
    end
  end
end
