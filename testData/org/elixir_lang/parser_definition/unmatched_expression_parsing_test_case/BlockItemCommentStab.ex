try do
catch
  # A user could create an error that looks like a builtin one
  # causing an error.
  :error, _ ->
    inspect(reason)
end