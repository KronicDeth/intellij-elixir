identifier do after expression end
identifier do after; expression; end
identifier do
  after expression
end
identifier do
  after
  expression
end

identifier do after -> end
identifier do after -> ; end
identifier do
  after ->
end
identifier do
  after
  ->
end

identifier do after -> expression end
identifier do afer -> expression; end
identifier do
  after -> expression
end
identifier do
  after
    ->
      expression
end

identifier do after () -> expression end
identifier do after () -> expression; end
identifier do
  after () -> expression
end
identifier do
  after
    ()
      ->
        expression
end

identifier do after expression -> end
identifier do after expression ->; end
identifier do
  after
    expression
      ->
end

identifier do after (key: value) -> end
identifier do after (key: value) ->; end
identifier do
  after
    (key: value)
      ->
end

identifier do after (one, two) -> end
identifier do after (one, two) ->; end
identifier do
  after
    (one, two)
      ->
end

identifier do after (one, key: value) -> end
identifier do after (one, key: value) ->; end
identifier do
  after
    (one, key: value)
      ->
end

identifier do after (one, two: 2) when three -> end
identifier do after (one, two: 2) when three ->; end
identifier do
  after
    (one, two: 2) when three
      ->
end

identifier do after (one, two: 2) when three -> four end
identifier do after (one, two: 2) when three -> four; end
identifier do
  after (one, two: 2) when three
    ->
      four
end

identifier do after key: value -> end
identifier do after key: value ->; end
identifier do
  after key: value
    ->
end

identifier do after one, two -> end
identifier do after one, two ->; end
identifier do
  after
    one, two
      ->
end

identifier do after one, key: value -> end
identifier do after one, key: value ->; end
identifier do
  after
    one, key: value
      ->
end

identifier do after one, two: 2 when three -> end
identifier do after one, two: 2 when three ->; end
identifier do
  after one, two: 2 when three
    ->
end

identifier do after one, two: 2 when three -> four end
identifier do after one, two: 2 when three -> four; end
identifier do
  after one, two: 2 when three
    ->
      four
end

identifier do
  after
    (one_a, two_a: 2) when three_a
      ->
        four_a
  after
    (one_b, two_b: -2) when three_b
      ->
        four_b
end
