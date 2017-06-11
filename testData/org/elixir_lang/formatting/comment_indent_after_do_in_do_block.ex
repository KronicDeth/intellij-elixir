defmodule Calcinator.JaSerializer.PhoenixView do
  defp params_to_render_opts(params) when is_map(params) do
    # must only add :include to opts if "include" is in params so that default includes don't get overridden
    case Map.fetch(params, "include") do
      {:ok, include} ->
        [include: include]
      :error ->
        []
    end
  end
end
