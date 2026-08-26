defmodule Macros do
  defmacro session(id, user) do
    quote do
      {:session, unquote(id), unquote(user)}
    end
  end
end

defmodule Consumer do
  import Macros

  def describe(raw) do
    session(id, user) = raw

    <caret>id
  end
end
