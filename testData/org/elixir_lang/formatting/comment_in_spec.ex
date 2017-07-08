defmodule Retort.Request do
  @spec from_string(String.t) ::
          {:ok, t} |
          # Poison 2.0
          {:error, :invalid} |
          {:error, {:invalid, token :: String.t}} |
          # Poison 3.0
          {:error, :invalid, position :: non_neg_integer} |
          {:error, {:invalid, token :: String.t, position :: non_neg_integer}}
end
