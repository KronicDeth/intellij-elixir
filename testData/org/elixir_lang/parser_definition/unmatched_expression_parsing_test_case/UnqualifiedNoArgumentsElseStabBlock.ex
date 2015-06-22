identifier do else expression end
identifier do else; expression; end
identifier do
  else expression
end
identifier do
  else
  expression
end

identifier do else -> end
identifier do else -> ; end
identifier do
  else ->
end
identifier do
  else
  ->
end

identifier do else -> expression end
identifier do afer -> expression; end
identifier do
  else -> expression
end
identifier do
  else
    ->
      expression
end

identifier do else () -> expression end
identifier do else () -> expression; end
identifier do
  else () -> expression
end
identifier do
  else
    ()
      ->
        expression
end

identifier do else expression -> end
identifier do else expression ->; end
identifier do
  else
    expression
      ->
end

identifier do else (key: value) -> end
identifier do else (key: value) ->; end
identifier do
  else
    (key: value)
      ->
end

identifier do else (one, two) -> end
identifier do else (one, two) ->; end
identifier do
  else
    (one, two)
      ->
end

identifier do else (one, key: value) -> end
identifier do else (one, key: value) ->; end
identifier do
  else
    (one, key: value)
      ->
end

identifier do else (one, two: 2) when three -> end
identifier do else (one, two: 2) when three ->; end
identifier do
  else
    (one, two: 2) when three
      ->
end

identifier do else (one, two: 2) when three -> four end
identifier do else (one, two: 2) when three -> four; end
identifier do
  else (one, two: 2) when three
    ->
      four
end

identifier do else key: value -> end
identifier do else key: value ->; end
identifier do
  else key: value
    ->
end

identifier do else one, two -> end
identifier do else one, two ->; end
identifier do
  else
    one, two
      ->
end

identifier do else one, key: value -> end
identifier do else one, key: value ->; end
identifier do
  else
    one, key: value
      ->
end

identifier do else one, two: 2 when three -> end
identifier do else one, two: 2 when three ->; end
identifier do
  else one, two: 2 when three
    ->
end

identifier do else one, two: 2 when three -> four end
identifier do else one, two: 2 when three -> four; end
identifier do
  else one, two: 2 when three
    ->
      four
end

identifier do
  else
    (one_a, two_a: 2) when three_a
      ->
        four_a
  else
    (one_b, two_b: -2) when three_b
      ->
        four_b
end
