defp predicate_list([h | t], initial, fun) do
  if !!fun.(h) == initial do
    predicate_list(t, initial, fun)
  else
    not initial
  end
end

not Code.ensure_loaded?(Hex)
(&1 in ?0..?9)
&(&1 in ?0..?9)
fn {k, _} -> k not in keys end
fn {k, _} -> k in keys end
fn {k, _} -> (k in keys) end
&1 in ?0..?9
&1 not in ?0..?9
&1 not in ?0..?9
&1 in ?0..?9
