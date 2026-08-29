defmodule VarBangDecl do
  defmacro assign_it do
    quote do
      var!(fo<caret>o) = 1
    end
  end
end
