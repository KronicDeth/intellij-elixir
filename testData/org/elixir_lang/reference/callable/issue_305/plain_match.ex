defmodule Consumer do
  def describe(raw) do
    {:session, id, user} = raw

    <caret>id
  end
end
