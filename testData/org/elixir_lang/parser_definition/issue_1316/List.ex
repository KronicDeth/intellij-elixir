case index do
  -1 ->
    list ++ [value]

  _ when index < 0 ->
    case length(list) + index + 1 do
      index when index < 0 -> [value | list]
      index -> do_insert_at(list, index, value)
    end

  _ ->
    do_insert_at(list, index, value)
end
