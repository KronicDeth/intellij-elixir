for {field, value} <- Map.from_struct(resource), value != nil, into: %{}, do: {field, value}
