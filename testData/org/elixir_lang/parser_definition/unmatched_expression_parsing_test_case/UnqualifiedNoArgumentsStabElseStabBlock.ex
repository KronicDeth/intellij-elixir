#
# identifier do expression else * end
#

identifier do one else two end
identifier do one; else; two; end
identifier do
  one else two
end
identifier do
  one
  else
  two
end

identifier do one else -> end
identifier do one else -> ; end
identifier do
  one else ->
end
identifier do
  one
  else
  ->
end

identifier do one else -> two end
identifier do one; else -> two; end
identifier do
  one else -> two
end
identifier do
  one
  else
    ->
      two
end

identifier do one else () -> two end
identifier do one; else () -> two; end
identifier do
  one else () -> two
end
identifier do
  one
  else
    ()
      ->
        two
end

identifier do one else two -> end
identifier do one else two ->; end
identifier do
  one
  else
    two
      ->
end

identifier do one else (key: value) -> end
identifier do one else (key: value) ->; end
identifier do
  one
  else
    (key: value)
      ->
end

identifier do one else (two, three) -> end
identifier do one else (two, three) ->; end
identifier do
  one
  else
    (two, three)
      ->
end

identifier do one else (two, key: value) -> end
identifier do one else (two, key: value) ->; end
identifier do
  one
  else
    (two, key: value)
      ->
end

identifier do one else (two, three: 3) when four -> end
identifier do one else (two, three: 3) when four ->; end
identifier do
  one
  else
    (two, three: 3) when four
      ->
end

identifier do one else (two, three: 3) when four -> five end
identifier do one else (two, three: 3) when four -> five; end
identifier do
  one else (two, three: 3) when four
    ->
      five
end

identifier do one else key: value -> end
identifier do one else key: value ->; end
identifier do
  one else key: value
    ->
end

identifier do one else two, three -> end
identifier do one else two, three ->; end
identifier do
  one
  else
    two, three
      ->
end

identifier do one else two, key: value -> end
identifier do one else two, key: value ->; end
identifier do
  one
  else
    two, key: value
      ->
end

identifier do one else two, three: 3 when four -> end
identifier do one else two, three: 3 when four ->; end
identifier do
  one
  else two, three: 3 when four
    ->
end

identifier do one else two, three: 3 when four -> five end
identifier do one else two, three: 3 when four -> five; end
identifier do
  one else two, three: 3 when four
    ->
      five
end

identifier do
  one
  else
    (two_a, three_a: 3) when four_a
      ->
        five_a
  else
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do -> else * end
#

identifier do -> else two end
identifier do ->; else; two; end
identifier do
  -> else two
end
identifier do
  ->
  else
  two
end

identifier do -> else -> end
identifier do -> else -> ; end
identifier do
  -> else ->
end
identifier do
  ->
  else
  ->
end

identifier do -> else -> two end
identifier do ->; else -> two; end
identifier do
  -> else -> two
end
identifier do
  ->
  else
    ->
      two
end

identifier do -> else () -> two end
identifier do ->; else () -> two; end
identifier do
  -> else () -> two
end
identifier do
  ->
  else
    ()
      ->
        two
end

identifier do -> else two -> end
identifier do -> else two ->; end
identifier do
  ->
  else
    two
      ->
end

identifier do -> else (key: value) -> end
identifier do -> else (key: value) ->; end
identifier do
  ->
  else
    (key: value)
      ->
end

identifier do -> else (two, three) -> end
identifier do -> else (two, three) ->; end
identifier do
  ->
  else
    (two, three)
      ->
end

identifier do -> else (two, key: value) -> end
identifier do -> else (two, key: value) ->; end
identifier do
  ->
  else
    (two, key: value)
      ->
end

identifier do -> else (two, three: 3) when four -> end
identifier do -> else (two, three: 3) when four ->; end
identifier do
  ->
  else
    (two, three: 3) when four
      ->
end

identifier do -> else (two, three: 3) when four -> five end
identifier do -> else (two, three: 3) when four -> five; end
identifier do
  -> else (two, three: 3) when four
    ->
      five
end

identifier do -> else key: value -> end
identifier do -> else key: value ->; end
identifier do
  -> else key: value
    ->
end

identifier do -> else two, three -> end
identifier do -> else two, three ->; end
identifier do
  ->
  else
    two, three
      ->
end

identifier do -> else two, key: value -> end
identifier do -> else two, key: value ->; end
identifier do
  ->
  else
    two, key: value
      ->
end

identifier do -> else two, three: 3 when four -> end
identifier do -> else two, three: 3 when four ->; end
identifier do
  ->
  else two, three: 3 when four
    ->
end

identifier do -> else two, three: 3 when four -> five end
identifier do -> else two, three: 3 when four -> five; end
identifier do
  -> else two, three: 3 when four
    ->
      five
end

identifier do
  ->
  else
    (two_a, three_a: 3) when four_a
      ->
        five_a
  else
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do -> expression else * end
#

identifier do -> one else two end
identifier do -> one; else; two; end
identifier do
  -> one else two
end
identifier do
  ->
  one
  else
  two
end

identifier do -> one else -> end
identifier do -> one else -> ; end
identifier do
  -> one else ->
end
identifier do
  ->
  one
  else
  ->
end

identifier do -> one else -> two end
identifier do -> one; else -> two; end
identifier do
  -> one else -> two
end
identifier do
  ->
  one
  else
    ->
      two
end

identifier do -> one else () -> two end
identifier do -> one; else () -> two; end
identifier do
  -> one else () -> two
end
identifier do
  ->
  one
  else
    ()
      ->
        two
end

identifier do -> one else two -> end
identifier do -> one else two ->; end
identifier do
  ->
  one
  else
    two
      ->
end

identifier do -> one else (key: value) -> end
identifier do -> one else (key: value) ->; end
identifier do
  ->
  one
  else
    (key: value)
      ->
end

identifier do -> one else (two, three) -> end
identifier do -> one else (two, three) ->; end
identifier do
  ->
  one
  else
    (two, three)
      ->
end

identifier do -> one else (two, key: value) -> end
identifier do -> one else (two, key: value) ->; end
identifier do
  ->
  one
  else
    (two, key: value)
      ->
end

identifier do -> one else (two, three: 3) when four -> end
identifier do -> one else (two, three: 3) when four ->; end
identifier do
  ->
  one
  else
    (two, three: 3) when four
      ->
end

identifier do -> one else (two, three: 3) when four -> five end
identifier do -> one else (two, three: 3) when four -> five; end
identifier do
  ->
  one
  else (two, three: 3) when four
    ->
      five
end

identifier do -> one else key: value -> end
identifier do -> one else key: value ->; end
identifier do
  ->
  one
  else key: value
    ->
end

identifier do -> one else two, three -> end
identifier do -> one else two, three ->; end
identifier do
  ->
  one
  else
    two, three
      ->
end

identifier do -> one else two, key: value -> end
identifier do -> one else two, key: value ->; end
identifier do
  ->
  one
  else
    two, key: value
      ->
end

identifier do -> one else two, three: 3 when four -> end
identifier do -> one else two, three: 3 when four ->; end
identifier do
  ->
  one
  else two, three: 3 when four
    ->
end

identifier do -> one else two, three: 3 when four -> five end
identifier do -> one else two, three: 3 when four -> five; end
identifier do
  ->
  one
  else two, three: 3 when four
    ->
      five
end

identifier do
  ->
  one
  else
    (two_a, three_a: 3) when four_a
      ->
        five_a
  else
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do () -> expression else * end
#

identifier do () -> one else two end
identifier do () -> one; else; two; end
identifier do
  () -> one else two
end
identifier do
  ()
  ->
  one
  else
  two
end

identifier do () -> one else -> end
identifier do () -> one else -> ; end
identifier do
  () -> one else ->
end
identifier do
  ()
  ->
  one
  else
  ->
end

identifier do () -> one else -> two end
identifier do () -> one; else -> two; end
identifier do
  () -> one else -> two
end
identifier do
  ()
  ->
  one
  else
    ->
      two
end

identifier do () -> one else () -> two end
identifier do () -> one; else () -> two; end
identifier do
  () -> one else () -> two
end
identifier do
  ()
  ->
  one
  else
    ()
      ->
        two
end

identifier do () -> one else two -> end
identifier do () -> one else two ->; end
identifier do
  ()
  ->
  one
  else
    two
      ->
end

identifier do () -> one else (key: value) -> end
identifier do () -> one else (key: value) ->; end
identifier do
  ()
  ->
  one
  else
    (key: value)
      ->
end

identifier do () -> one else (two, three) -> end
identifier do () -> one else (two, three) ->; end
identifier do
  ()
  ->
  one
  else
    (two, three)
      ->
end

identifier do () -> one else (two, key: value) -> end
identifier do () -> one else (two, key: value) ->; end
identifier do
  ()
  ->
  one
  else
    (two, key: value)
      ->
end

identifier do () -> one else (two, three: 3) when four -> end
identifier do () -> one else (two, three: 3) when four ->; end
identifier do
  ()
  ->
  one
  else
    (two, three: 3) when four
      ->
end

identifier do () -> one else (two, three: 3) when four -> five end
identifier do () -> one else (two, three: 3) when four -> five; end
identifier do
  ()
  ->
  one
  else (two, three: 3) when four
    ->
      five
end

identifier do () -> one else key: value -> end
identifier do () -> one else key: value ->; end
identifier do
  ()
  ->
  one
  else key: value
    ->
end

identifier do () -> one else two, three -> end
identifier do () -> one else two, three ->; end
identifier do
  ()
  ->
  one
  else
    two, three
      ->
end

identifier do () -> one else two, key: value -> end
identifier do () -> one else two, key: value ->; end
identifier do
  ()
  ->
  one
  else
    two, key: value
      ->
end

identifier do () -> one else two, three: 3 when four -> end
identifier do () -> one else two, three: 3 when four ->; end
identifier do
  ()
  ->
  one
  else two, three: 3 when four
    ->
end

identifier do () -> one else two, three: 3 when four -> five end
identifier do () -> one else two, three: 3 when four -> five; end
identifier do
  ()
  ->
  one
  else two, three: 3 when four
    ->
      five
end

identifier do
  ()
  ->
  one
  else
    (two_a, three_a: 3) when four_a
      ->
        five_a
  else
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do expression -> else * end
#

identifier do one -> else two end
identifier do one ->; else; two; end
identifier do
  one -> else two
end
identifier do
  one
  ->
  else
  two
end

identifier do one -> else -> end
identifier do one -> else -> ; end
identifier do
  one -> else ->
end
identifier do
  one
  ->
  else
  ->
end

identifier do one -> else -> two end
identifier do one ->; else -> two; end
identifier do
  one -> else -> two
end
identifier do
  one
  ->
  else
    ->
      two
end

identifier do one -> else () -> two end
identifier do one ->; else () -> two; end
identifier do
  one -> else () -> two
end
identifier do
  one
  ->
  else
    ()
      ->
        two
end

identifier do one -> else two -> end
identifier do one -> else two ->; end
identifier do
  one
  ->
  else
    two
      ->
end

identifier do one -> else (key: value) -> end
identifier do one -> else (key: value) ->; end
identifier do
  one
  ->
  else
    (key: value)
      ->
end

identifier do one -> else (two, three) -> end
identifier do one -> else (two, three) ->; end
identifier do
  one
  ->
  else
    (two, three)
      ->
end

identifier do one -> else (two, key: value) -> end
identifier do one -> else (two, key: value) ->; end
identifier do
  one
  ->
  else
    (two, key: value)
      ->
end

identifier do one -> else (two, three: 3) when four -> end
identifier do one -> else (two, three: 3) when four ->; end
identifier do
  one
  ->
  else
    (two, three: 3) when four
      ->
end

identifier do one -> else (two, three: 3) when four -> five end
identifier do one -> else (two, three: 3) when four -> five; end
identifier do
  one
  ->
  else (two, three: 3) when four
    ->
      five
end

identifier do one -> else key: value -> end
identifier do one -> else key: value ->; end
identifier do
  one
  ->
  else key: value
    ->
end

identifier do one -> else two, three -> end
identifier do one -> else two, three ->; end
identifier do
  one
  ->
  else
    two, three
      ->
end

identifier do one -> else two, key: value -> end
identifier do one -> else two, key: value ->; end
identifier do
  one
  ->
  else
    two, key: value
      ->
end

identifier do one -> else two, three: 3 when four -> end
identifier do one -> else two, three: 3 when four ->; end
identifier do
  one
  ->
  else two, three: 3 when four
    ->
end

identifier do one -> else two, three: 3 when four -> five end
identifier do one -> else two, three: 3 when four -> five; end
identifier do
  one
  ->
  else two, three: 3 when four
    ->
      five
end

identifier do
  one
  ->
  else
    (two_a, three_a: 3) when four_a
      ->
        five_a
  else
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do (key: value) -> else * end
#

identifier do (key: value) -> else two end
identifier do (key: value) ->; else; two; end
identifier do
  (key: value) -> else two
end
identifier do
  (key: value)
  ->
  else
  two
end

identifier do (key: value) -> else -> end
identifier do (key: value) -> else -> ; end
identifier do
  (key: value) -> else ->
end
identifier do
  (key: value)
  ->
  else
  ->
end

identifier do (key: value) -> else -> two end
identifier do (key: value) ->; else -> two; end
identifier do
  (key: value) -> else -> two
end
identifier do
  (key: value)
  ->
  else
    ->
      two
end

identifier do (key: value) -> else () -> two end
identifier do (key: value) ->; else () -> two; end
identifier do
  (key: value) -> else () -> two
end
identifier do
  (key: value)
  ->
  else
    ()
      ->
        two
end

identifier do (key: value) -> else two -> end
identifier do (key: value) -> else two ->; end
identifier do
  (key: value)
  ->
  else
    two
      ->
end

identifier do (key: value) -> else (key: value) -> end
identifier do (key: value) -> else (key: value) ->; end
identifier do
  (key: value)
  ->
  else
    (key: value)
      ->
end

identifier do (key: value) -> else (two, three) -> end
identifier do (key: value) -> else (two, three) ->; end
identifier do
  (key: value)
  ->
  else
    (two, three)
      ->
end

identifier do (key: value) -> else (two, key: value) -> end
identifier do (key: value) -> else (two, key: value) ->; end
identifier do
  (key: value)
  ->
  else
    (two, key: value)
      ->
end

identifier do (key: value) -> else (two, three: 3) when four -> end
identifier do (key: value) -> else (two, three: 3) when four ->; end
identifier do
  (key: value)
  ->
  else
    (two, three: 3) when four
      ->
end

identifier do (key: value) -> else (two, three: 3) when four -> five end
identifier do (key: value) -> else (two, three: 3) when four -> five; end
identifier do
  (key: value)
  ->
  else (two, three: 3) when four
    ->
      five
end

identifier do (key: value) -> else key: value -> end
identifier do (key: value) -> else key: value ->; end
identifier do
  (key: value)
  ->
  else key: value
    ->
end

identifier do (key: value) -> else two, three -> end
identifier do (key: value) -> else two, three ->; end
identifier do
  (key: value)
  ->
  else
    two, three
      ->
end

identifier do (key: value) -> else two, key: value -> end
identifier do (key: value) -> else two, key: value ->; end
identifier do
  (key: value)
  ->
  else
    two, key: value
      ->
end

identifier do (key: value) -> else two, three: 3 when four -> end
identifier do (key: value) -> else two, three: 3 when four ->; end
identifier do
  (key: value)
  ->
  else two, three: 3 when four
    ->
end

identifier do (key: value) -> else two, three: 3 when four -> five end
identifier do (key: value) -> else two, three: 3 when four -> five; end
identifier do
  (key: value)
  ->
  else two, three: 3 when four
    ->
      five
end

identifier do
  (key: value)
  ->
  else
    (two_a, three_a: 3) when four_a
      ->
        five_a
  else
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do (one, two) -> else * end
#

identifier do (one, two) -> else three end
identifier do (one, two) ->; else; three; end
identifier do
  (one, two) -> else three
end
identifier do
  (one, two)
  ->
  else
  three
end

identifier do (one, two) -> else -> end
identifier do (one, two) -> else -> ; end
identifier do
  (one, two) -> else ->
end
identifier do
  (one, two)
  ->
  else
  ->
end

identifier do (one, two) -> else -> three end
identifier do (one, two) ->; else -> three; end
identifier do
  (one, two) -> else -> two
end
identifier do
  (one, two)
  ->
  else
    ->
      three
end

identifier do (one, two) -> else () -> three end
identifier do (one, two) ->; else () -> three; end
identifier do
  (one, two) -> else () -> three
end
identifier do
  (one, two)
  ->
  else
    ()
      ->
        three
end

identifier do (one, two) -> else three -> end
identifier do (one, two) -> else three ->; end
identifier do
  (one, two)
  ->
  else
    three
      ->
end

identifier do (one, two) -> else (three, four) -> end
identifier do (one, two) -> else (three, four) ->; end
identifier do
  (one, two)
  ->
  else
    (three, four)
    ->
end

identifier do (one, two) -> else (three, four) -> end
identifier do (one, two) -> else (three, four) ->; end
identifier do
  (one, two)
  ->
  else
    (three, four)
      ->
end

identifier do (one, two) -> else (three, key: value) -> end
identifier do (one, two) -> else (three, key: value) ->; end
identifier do
  (one, two)
  ->
  else
    (three, key: value)
      ->
end

identifier do (one, two) -> else (three, four: 4) when five -> end
identifier do (one, two) -> else (three, four: 4) when five ->; end
identifier do
  (one, two)
  ->
  else
    (three, four: 4) when five
      ->
end

identifier do (one, two) -> else (three, four: 4) when five -> six end
identifier do (one, two) -> else (three, four: 4) when five -> six; end
identifier do
  (one, two)
  ->
  else (three, four: 4) when five
    ->
      six
end

identifier do (one, two) -> else key: value -> end
identifier do (one, two) -> else key: value ->; end
identifier do
  (one, two)
  ->
  else key: value
    ->
end

identifier do (one, two) -> else three, four -> end
identifier do (one, two) -> else three, four ->; end
identifier do
  (one, two)
  ->
  else
    three, four
      ->
end

identifier do (one, two) -> else three, key: value -> end
identifier do (one, two) -> else three, key: value ->; end
identifier do
  (one, two)
  ->
  else
    three, key: value
      ->
end

identifier do (one, two) -> else three, four: 4 when five -> end
identifier do (one, two) -> else three, four: 4 when five ->; end
identifier do
  (one, two)
  ->
  else three, four: 4 when five
    ->
end

identifier do (one, two) -> else three, four: 4 when five -> six end
identifier do (one, two) -> else three, four: 4 when five -> six; end
identifier do
  (one, two)
  ->
  else three, four: 4 when five
    ->
      six
end

identifier do
  (one, two)
  ->
  else
    (three_a, four_a: 4) when five_a
      ->
        six_a
  else
    (three_b, four_b: -4) when five_b
      ->
        six_b
end

#
# 8 more variants left to the reader
#
