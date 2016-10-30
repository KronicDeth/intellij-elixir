defmodule Postgrex.Messages do
  # ...
  Enum.each(@auth_types, fn {type, value} ->
    def decode_auth_type(unquote(value)), do: unquote(type)
  end)
  # ...
end
