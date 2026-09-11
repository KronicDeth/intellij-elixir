Enum.reduce(
  for elem1 <- 1..5, elem2 <- 1..5 do
    {elem1, elem2}
  end,
  acc,
  &union/2
)
