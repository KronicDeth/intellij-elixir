identifier do rescue expression end
identifier do rescue; expression; end
identifier do
  rescue expression
end
identifier do
  rescue
  expression
end

identifier do rescue -> end
identifier do rescue -> ; end
identifier do
  rescue ->
end
identifier do
  rescue
  ->
end

identifier do rescue -> expression end
identifier do afer -> expression; end
identifier do
  rescue -> expression
end
identifier do
  rescue
    ->
      expression
end

identifier do rescue () -> expression end
identifier do rescue () -> expression; end
identifier do
  rescue () -> expression
end
identifier do
  rescue
    ()
      ->
        expression
end

identifier do rescue expression -> end
identifier do rescue expression ->; end
identifier do
  rescue
    expression
      ->
end

identifier do rescue (key: value) -> end
identifier do rescue (key: value) ->; end
identifier do
  rescue
    (key: value)
      ->
end

identifier do rescue (one, two) -> end
identifier do rescue (one, two) ->; end
identifier do
  rescue
    (one, two)
      ->
end

identifier do rescue (one, key: value) -> end
identifier do rescue (one, key: value) ->; end
identifier do
  rescue
    (one, key: value)
      ->
end

identifier do rescue (one, two: 2) when three -> end
identifier do rescue (one, two: 2) when three ->; end
identifier do
  rescue
    (one, two: 2) when three
      ->
end

identifier do rescue (one, two: 2) when three -> four end
identifier do rescue (one, two: 2) when three -> four; end
identifier do
  rescue (one, two: 2) when three
    ->
      four
end

identifier do rescue key: value -> end
identifier do rescue key: value ->; end
identifier do
  rescue key: value
    ->
end

identifier do rescue one, two -> end
identifier do rescue one, two ->; end
identifier do
  rescue
    one, two
      ->
end

identifier do rescue one, key: value -> end
identifier do rescue one, key: value ->; end
identifier do
  rescue
    one, key: value
      ->
end

identifier do rescue one, two: 2 when three -> end
identifier do rescue one, two: 2 when three ->; end
identifier do
  rescue one, two: 2 when three
    ->
end

identifier do rescue one, two: 2 when three -> four end
identifier do rescue one, two: 2 when three -> four; end
identifier do
  rescue one, two: 2 when three
    ->
      four
end

identifier do
  rescue
    (one_a, two_a: 2) when three_a
      ->
        four_a
  rescue
    (one_b, two_b: -2) when three_b
      ->
        four_b
end
