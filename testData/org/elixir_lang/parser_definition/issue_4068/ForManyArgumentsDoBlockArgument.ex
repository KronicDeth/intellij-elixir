catch_error(
  for x <- 1..3, into: %Pdict{} do
    if x > 2, do: raise("oops"), else: x
  end
)
