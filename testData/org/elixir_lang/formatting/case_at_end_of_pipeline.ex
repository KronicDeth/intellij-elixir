defmodule Calcinator.Meta.Beam do
  def decode(encoded) do
    # See https://github.com/phoenixframework/phoenix_ecto/blob/90ba79feef55e31573047f789b3561f4ab7f30f6/lib/
    #   phoenix_ecto/sql/sandbox.ex#L73-L79
    encoded
    |> Base.url_decode64!
    |> :erlang.binary_to_term
    |> case do
         {@version, map} when is_map(map) -> map
         _ -> %{}
       end
  end
end
