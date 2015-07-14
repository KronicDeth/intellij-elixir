fn
  # OTP 18
  {:type, _, :map_field_assoc, [k, v]} ->
    {typespec_to_ast(k), typespec_to_ast(v)}
  # OTP 17
  {:type, _, :map_field_assoc, k, v} ->
    {typespec_to_ast(k), typespec_to_ast(v)}
end