# https://github.com/elixir-lang/elixir/blob/e49a806e8abe43c62c9c1b21ea7eb2217bef1966/lib/elixir/test/elixir/kernel/quote_test.exs#L97
fn(unquote_splicing([1, 2, 3])) -> :ok end
# https://github.com/elixir-lang/elixir/blob/e49a806e8abe43c62c9c1b21ea7eb2217bef1966/lib/elixir/test/elixir/kernel/quote_test.exs#L101
fn(1, unquote_splicing([2, 3])) -> :ok end

# same as above, just for no parentheses
fn unquote_splicing([1, 2, 3]) -> :ok end
fn 1, unquote_splicing([2, 3]) -> :ok end
