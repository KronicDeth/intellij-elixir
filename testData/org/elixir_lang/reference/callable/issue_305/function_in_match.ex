defmodule Functions do
  def session(id, user), do: {:session, id, user}
end

defmodule Consumer do
  import Functions

  def describe(raw) do
    session(id, user) = raw

    <caret>id
  end
end
