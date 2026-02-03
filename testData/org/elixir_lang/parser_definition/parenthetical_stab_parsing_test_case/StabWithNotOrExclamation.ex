Enum.split_while(&(&1 not in ?0..?9))
:lists.filter(fn {k, _} -> k not in keys end, keywords)
fun = fn
{key, _value} when is_atom(key) ->
  not has_key?(keywords2, key)

_ ->
  raise ArgumentError,
        "expected a keyword list as the first argument, got: #{inspect(keywords1)}"
end
