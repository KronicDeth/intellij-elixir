def foo a when is_list(a) do
  a
end

@spec foo(a) when a: list

def foo(a) when is_list(a) do
  a
end

fn a when is_list(a) -> a end

fn (a) when is_list(a) -> a end

fn
  (list) when is_list(list) -> list
  map when is_map(map) -> map
end

case result do
  {:ok, list} when is_list(list) -> list
  {:error, reason} when is_binary(reason) -> reason
end
