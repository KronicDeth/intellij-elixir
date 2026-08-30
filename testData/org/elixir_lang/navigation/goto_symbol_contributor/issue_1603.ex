defmodule Issue1603 do
  defdelegate collide(argument), to: Issue1603.First

  defdelegate collide(argument), to: Issue1603.Second
end
