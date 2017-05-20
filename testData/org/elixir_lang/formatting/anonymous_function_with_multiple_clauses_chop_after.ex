fn
  list when is_list(list) ->
    list ++ ~w(a very long line that should trigger the rest of the stab bodies in to be chopped down)
  map when is_map(map) ->
    map
end
