identifier do expression end
identifier do expression; end
identifier do
  expression
end

identifier do -> end
identifier do -> ; end
identifier do
  ->
end

identifier do -> expression end
identifier do -> expression; end
identifier do
  -> expression
end
identifier do
  ->
    expression
end

identifier do () -> expression end
identifier do () -> expression; end
identifier do
  () -> expression
end
identifier do
  () ->
    expression
end

identifier do expression -> end
identifier do expression ->; end
identifier do
  expression ->
end

identifier do (key: value) -> end
identifier do (key: value) ->; end
identifier do
  (key: value) ->
end

identifier do (one, two) -> end
identifier do (one, two) ->; end
identifier do
  (one, two) ->
end

identifier do (one, key: value) -> end
identifier do (one, key: value) ->; end
identifier do
  (one, key: value) ->
end

identifier do (one, two: 2) when three -> end
identifier do (one, two: 2) when three ->; end
identifier do
  (one, two: 2) when three ->
end

identifier do (one, two: 2) when three -> four end
identifier do (one, two: 2) when three -> four; end
identifier do
  (one, two: 2) when three ->
    four
end

identifier do key: value -> end
identifier do key: value ->; end
identifier do
  key: value ->
end

identifier do one, two -> end
identifier do one, two ->; end
identifier do
  one, two ->
end

identifier do one, key: value -> end
identifier do one, key: value ->; end
identifier do
  one, key: value ->
end

identifier do one, two: 2 when three -> end
identifier do one, two: 2 when three ->; end
identifier do
  one, two: 2 when three ->
end

identifier do one, two: 2 when three -> four end
identifier do one, two: 2 when three -> four; end
identifier do
  one, two: 2 when three ->
    four
end

identifier do
  (one_a, two_a: 2) when three_a ->
    four_a
  (one_b, two_b: -2) when three_b ->
    four_b
end
