defmodule Calcinator.Resources.Ecto.Repo do
  defp unload_preloads(updated, preloads) do
    Enum.reduce(
      preloads,
      updated,
      fn
        # preloads = [:<field>]
        (field, acc) when is_atom(field) ->
          Map.put(acc, field, Map.get(acc.__struct__.__struct__, field))
        # preloads = [<field>: <association_preloads>]
        ({field, _}, acc) when is_atom(field) ->
          Map.put(acc, field, Map.get(acc.__struct__.__struct__, field))
      end
    )
  end
end
