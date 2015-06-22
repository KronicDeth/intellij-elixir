#
# identifier do expression after * end
#

identifier do one after two end
identifier do one; after; two; end
identifier do
  one after two
end
identifier do
  one
  after
  two
end

identifier do one after -> end
identifier do one after -> ; end
identifier do
  one after ->
end
identifier do
  one
  after
  ->
end

identifier do one after -> two end
identifier do one; after -> two; end
identifier do
  one after -> two
end
identifier do
  one
  after
    ->
      two
end

identifier do one after () -> two end
identifier do one; after () -> two; end
identifier do
  one after () -> two
end
identifier do
  one
  after
    ()
      ->
        two
end

identifier do one after two -> end
identifier do one after two ->; end
identifier do
  one
  after
    two
      ->
end

identifier do one after (key: value) -> end
identifier do one after (key: value) ->; end
identifier do
  one
  after
    (key: value)
      ->
end

identifier do one after (two, three) -> end
identifier do one after (two, three) ->; end
identifier do
  one
  after
    (two, three)
      ->
end

identifier do one after (two, key: value) -> end
identifier do one after (two, key: value) ->; end
identifier do
  one
  after
    (two, key: value)
      ->
end

identifier do one after (two, three: 3) when four -> end
identifier do one after (two, three: 3) when four ->; end
identifier do
  one
  after
    (two, three: 3) when four
      ->
end

identifier do one after (two, three: 3) when four -> five end
identifier do one after (two, three: 3) when four -> five; end
identifier do
  one after (two, three: 3) when four
    ->
      five
end

identifier do one after key: value -> end
identifier do one after key: value ->; end
identifier do
  one after key: value
    ->
end

identifier do one after two, three -> end
identifier do one after two, three ->; end
identifier do
  one
  after
    two, three
      ->
end

identifier do one after two, key: value -> end
identifier do one after two, key: value ->; end
identifier do
  one
  after
    two, key: value
      ->
end

identifier do one after two, three: 3 when four -> end
identifier do one after two, three: 3 when four ->; end
identifier do
  one
  after two, three: 3 when four
    ->
end

identifier do one after two, three: 3 when four -> five end
identifier do one after two, three: 3 when four -> five; end
identifier do
  one after two, three: 3 when four
    ->
      five
end

identifier do
  one
  after
    (two_a, three_a: 3) when four_a
      ->
        five_a
  after
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do -> after * end
#

identifier do -> after two end
identifier do ->; after; two; end
identifier do
  -> after two
end
identifier do
  ->
  after
  two
end

identifier do -> after -> end
identifier do -> after -> ; end
identifier do
  -> after ->
end
identifier do
  ->
  after
  ->
end

identifier do -> after -> two end
identifier do ->; after -> two; end
identifier do
  -> after -> two
end
identifier do
  ->
  after
    ->
      two
end

identifier do -> after () -> two end
identifier do ->; after () -> two; end
identifier do
  -> after () -> two
end
identifier do
  ->
  after
    ()
      ->
        two
end

identifier do -> after two -> end
identifier do -> after two ->; end
identifier do
  ->
  after
    two
      ->
end

identifier do -> after (key: value) -> end
identifier do -> after (key: value) ->; end
identifier do
  ->
  after
    (key: value)
      ->
end

identifier do -> after (two, three) -> end
identifier do -> after (two, three) ->; end
identifier do
  ->
  after
    (two, three)
      ->
end

identifier do -> after (two, key: value) -> end
identifier do -> after (two, key: value) ->; end
identifier do
  ->
  after
    (two, key: value)
      ->
end

identifier do -> after (two, three: 3) when four -> end
identifier do -> after (two, three: 3) when four ->; end
identifier do
  ->
  after
    (two, three: 3) when four
      ->
end

identifier do -> after (two, three: 3) when four -> five end
identifier do -> after (two, three: 3) when four -> five; end
identifier do
  -> after (two, three: 3) when four
    ->
      five
end

identifier do -> after key: value -> end
identifier do -> after key: value ->; end
identifier do
  -> after key: value
    ->
end

identifier do -> after two, three -> end
identifier do -> after two, three ->; end
identifier do
  ->
  after
    two, three
      ->
end

identifier do -> after two, key: value -> end
identifier do -> after two, key: value ->; end
identifier do
  ->
  after
    two, key: value
      ->
end

identifier do -> after two, three: 3 when four -> end
identifier do -> after two, three: 3 when four ->; end
identifier do
  ->
  after two, three: 3 when four
    ->
end

identifier do -> after two, three: 3 when four -> five end
identifier do -> after two, three: 3 when four -> five; end
identifier do
  -> after two, three: 3 when four
    ->
      five
end

identifier do
  ->
  after
    (two_a, three_a: 3) when four_a
      ->
        five_a
  after
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do -> expression after * end
#

identifier do -> one after two end
identifier do -> one; after; two; end
identifier do
  -> one after two
end
identifier do
  ->
  one
  after
  two
end

identifier do -> one after -> end
identifier do -> one after -> ; end
identifier do
  -> one after ->
end
identifier do
  ->
  one
  after
  ->
end

identifier do -> one after -> two end
identifier do -> one; after -> two; end
identifier do
  -> one after -> two
end
identifier do
  ->
  one
  after
    ->
      two
end

identifier do -> one after () -> two end
identifier do -> one; after () -> two; end
identifier do
  -> one after () -> two
end
identifier do
  ->
  one
  after
    ()
      ->
        two
end

identifier do -> one after two -> end
identifier do -> one after two ->; end
identifier do
  ->
  one
  after
    two
      ->
end

identifier do -> one after (key: value) -> end
identifier do -> one after (key: value) ->; end
identifier do
  ->
  one
  after
    (key: value)
      ->
end

identifier do -> one after (two, three) -> end
identifier do -> one after (two, three) ->; end
identifier do
  ->
  one
  after
    (two, three)
      ->
end

identifier do -> one after (two, key: value) -> end
identifier do -> one after (two, key: value) ->; end
identifier do
  ->
  one
  after
    (two, key: value)
      ->
end

identifier do -> one after (two, three: 3) when four -> end
identifier do -> one after (two, three: 3) when four ->; end
identifier do
  ->
  one
  after
    (two, three: 3) when four
      ->
end

identifier do -> one after (two, three: 3) when four -> five end
identifier do -> one after (two, three: 3) when four -> five; end
identifier do
  ->
  one
  after (two, three: 3) when four
    ->
      five
end

identifier do -> one after key: value -> end
identifier do -> one after key: value ->; end
identifier do
  ->
  one
  after key: value
    ->
end

identifier do -> one after two, three -> end
identifier do -> one after two, three ->; end
identifier do
  ->
  one
  after
    two, three
      ->
end

identifier do -> one after two, key: value -> end
identifier do -> one after two, key: value ->; end
identifier do
  ->
  one
  after
    two, key: value
      ->
end

identifier do -> one after two, three: 3 when four -> end
identifier do -> one after two, three: 3 when four ->; end
identifier do
  ->
  one
  after two, three: 3 when four
    ->
end

identifier do -> one after two, three: 3 when four -> five end
identifier do -> one after two, three: 3 when four -> five; end
identifier do
  ->
  one
  after two, three: 3 when four
    ->
      five
end

identifier do
  ->
  one
  after
    (two_a, three_a: 3) when four_a
      ->
        five_a
  after
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do () -> expression after * end
#

identifier do () -> one after two end
identifier do () -> one; after; two; end
identifier do
  () -> one after two
end
identifier do
  ()
  ->
  one
  after
  two
end

identifier do () -> one after -> end
identifier do () -> one after -> ; end
identifier do
  () -> one after ->
end
identifier do
  ()
  ->
  one
  after
  ->
end

identifier do () -> one after -> two end
identifier do () -> one; after -> two; end
identifier do
  () -> one after -> two
end
identifier do
  ()
  ->
  one
  after
    ->
      two
end

identifier do () -> one after () -> two end
identifier do () -> one; after () -> two; end
identifier do
  () -> one after () -> two
end
identifier do
  ()
  ->
  one
  after
    ()
      ->
        two
end

identifier do () -> one after two -> end
identifier do () -> one after two ->; end
identifier do
  ()
  ->
  one
  after
    two
      ->
end

identifier do () -> one after (key: value) -> end
identifier do () -> one after (key: value) ->; end
identifier do
  ()
  ->
  one
  after
    (key: value)
      ->
end

identifier do () -> one after (two, three) -> end
identifier do () -> one after (two, three) ->; end
identifier do
  ()
  ->
  one
  after
    (two, three)
      ->
end

identifier do () -> one after (two, key: value) -> end
identifier do () -> one after (two, key: value) ->; end
identifier do
  ()
  ->
  one
  after
    (two, key: value)
      ->
end

identifier do () -> one after (two, three: 3) when four -> end
identifier do () -> one after (two, three: 3) when four ->; end
identifier do
  ()
  ->
  one
  after
    (two, three: 3) when four
      ->
end

identifier do () -> one after (two, three: 3) when four -> five end
identifier do () -> one after (two, three: 3) when four -> five; end
identifier do
  ()
  ->
  one
  after (two, three: 3) when four
    ->
      five
end

identifier do () -> one after key: value -> end
identifier do () -> one after key: value ->; end
identifier do
  ()
  ->
  one
  after key: value
    ->
end

identifier do () -> one after two, three -> end
identifier do () -> one after two, three ->; end
identifier do
  ()
  ->
  one
  after
    two, three
      ->
end

identifier do () -> one after two, key: value -> end
identifier do () -> one after two, key: value ->; end
identifier do
  ()
  ->
  one
  after
    two, key: value
      ->
end

identifier do () -> one after two, three: 3 when four -> end
identifier do () -> one after two, three: 3 when four ->; end
identifier do
  ()
  ->
  one
  after two, three: 3 when four
    ->
end

identifier do () -> one after two, three: 3 when four -> five end
identifier do () -> one after two, three: 3 when four -> five; end
identifier do
  ()
  ->
  one
  after two, three: 3 when four
    ->
      five
end

identifier do
  ()
  ->
  one
  after
    (two_a, three_a: 3) when four_a
      ->
        five_a
  after
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do expression -> after * end
#

identifier do one -> after two end
identifier do one ->; after; two; end
identifier do
  one -> after two
end
identifier do
  one
  ->
  after
  two
end

identifier do one -> after -> end
identifier do one -> after -> ; end
identifier do
  one -> after ->
end
identifier do
  one
  ->
  after
  ->
end

identifier do one -> after -> two end
identifier do one ->; after -> two; end
identifier do
  one -> after -> two
end
identifier do
  one
  ->
  after
    ->
      two
end

identifier do one -> after () -> two end
identifier do one ->; after () -> two; end
identifier do
  one -> after () -> two
end
identifier do
  one
  ->
  after
    ()
      ->
        two
end

identifier do one -> after two -> end
identifier do one -> after two ->; end
identifier do
  one
  ->
  after
    two
      ->
end

identifier do one -> after (key: value) -> end
identifier do one -> after (key: value) ->; end
identifier do
  one
  ->
  after
    (key: value)
      ->
end

identifier do one -> after (two, three) -> end
identifier do one -> after (two, three) ->; end
identifier do
  one
  ->
  after
    (two, three)
      ->
end

identifier do one -> after (two, key: value) -> end
identifier do one -> after (two, key: value) ->; end
identifier do
  one
  ->
  after
    (two, key: value)
      ->
end

identifier do one -> after (two, three: 3) when four -> end
identifier do one -> after (two, three: 3) when four ->; end
identifier do
  one
  ->
  after
    (two, three: 3) when four
      ->
end

identifier do one -> after (two, three: 3) when four -> five end
identifier do one -> after (two, three: 3) when four -> five; end
identifier do
  one
  ->
  after (two, three: 3) when four
    ->
      five
end

identifier do one -> after key: value -> end
identifier do one -> after key: value ->; end
identifier do
  one
  ->
  after key: value
    ->
end

identifier do one -> after two, three -> end
identifier do one -> after two, three ->; end
identifier do
  one
  ->
  after
    two, three
      ->
end

identifier do one -> after two, key: value -> end
identifier do one -> after two, key: value ->; end
identifier do
  one
  ->
  after
    two, key: value
      ->
end

identifier do one -> after two, three: 3 when four -> end
identifier do one -> after two, three: 3 when four ->; end
identifier do
  one
  ->
  after two, three: 3 when four
    ->
end

identifier do one -> after two, three: 3 when four -> five end
identifier do one -> after two, three: 3 when four -> five; end
identifier do
  one
  ->
  after two, three: 3 when four
    ->
      five
end

identifier do
  one
  ->
  after
    (two_a, three_a: 3) when four_a
      ->
        five_a
  after
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do (key: value) -> after * end
#

identifier do (key: value) -> after two end
identifier do (key: value) ->; after; two; end
identifier do
  (key: value) -> after two
end
identifier do
  (key: value)
  ->
  after
  two
end

identifier do (key: value) -> after -> end
identifier do (key: value) -> after -> ; end
identifier do
  (key: value) -> after ->
end
identifier do
  (key: value)
  ->
  after
  ->
end

identifier do (key: value) -> after -> two end
identifier do (key: value) ->; after -> two; end
identifier do
  (key: value) -> after -> two
end
identifier do
  (key: value)
  ->
  after
    ->
      two
end

identifier do (key: value) -> after () -> two end
identifier do (key: value) ->; after () -> two; end
identifier do
  (key: value) -> after () -> two
end
identifier do
  (key: value)
  ->
  after
    ()
      ->
        two
end

identifier do (key: value) -> after two -> end
identifier do (key: value) -> after two ->; end
identifier do
  (key: value)
  ->
  after
    two
      ->
end

identifier do (key: value) -> after (key: value) -> end
identifier do (key: value) -> after (key: value) ->; end
identifier do
  (key: value)
  ->
  after
    (key: value)
      ->
end

identifier do (key: value) -> after (two, three) -> end
identifier do (key: value) -> after (two, three) ->; end
identifier do
  (key: value)
  ->
  after
    (two, three)
      ->
end

identifier do (key: value) -> after (two, key: value) -> end
identifier do (key: value) -> after (two, key: value) ->; end
identifier do
  (key: value)
  ->
  after
    (two, key: value)
      ->
end

identifier do (key: value) -> after (two, three: 3) when four -> end
identifier do (key: value) -> after (two, three: 3) when four ->; end
identifier do
  (key: value)
  ->
  after
    (two, three: 3) when four
      ->
end

identifier do (key: value) -> after (two, three: 3) when four -> five end
identifier do (key: value) -> after (two, three: 3) when four -> five; end
identifier do
  (key: value)
  ->
  after (two, three: 3) when four
    ->
      five
end

identifier do (key: value) -> after key: value -> end
identifier do (key: value) -> after key: value ->; end
identifier do
  (key: value)
  ->
  after key: value
    ->
end

identifier do (key: value) -> after two, three -> end
identifier do (key: value) -> after two, three ->; end
identifier do
  (key: value)
  ->
  after
    two, three
      ->
end

identifier do (key: value) -> after two, key: value -> end
identifier do (key: value) -> after two, key: value ->; end
identifier do
  (key: value)
  ->
  after
    two, key: value
      ->
end

identifier do (key: value) -> after two, three: 3 when four -> end
identifier do (key: value) -> after two, three: 3 when four ->; end
identifier do
  (key: value)
  ->
  after two, three: 3 when four
    ->
end

identifier do (key: value) -> after two, three: 3 when four -> five end
identifier do (key: value) -> after two, three: 3 when four -> five; end
identifier do
  (key: value)
  ->
  after two, three: 3 when four
    ->
      five
end

identifier do
  (key: value)
  ->
  after
    (two_a, three_a: 3) when four_a
      ->
        five_a
  after
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do (one, two) -> after * end
#

identifier do (one, two) -> after three end
identifier do (one, two) ->; after; three; end
identifier do
  (one, two) -> after three
end
identifier do
  (one, two)
  ->
  after
  three
end

identifier do (one, two) -> after -> end
identifier do (one, two) -> after -> ; end
identifier do
  (one, two) -> after ->
end
identifier do
  (one, two)
  ->
  after
  ->
end

identifier do (one, two) -> after -> three end
identifier do (one, two) ->; after -> three; end
identifier do
  (one, two) -> after -> two
end
identifier do
  (one, two)
  ->
  after
    ->
      three
end

identifier do (one, two) -> after () -> three end
identifier do (one, two) ->; after () -> three; end
identifier do
  (one, two) -> after () -> three
end
identifier do
  (one, two)
  ->
  after
    ()
      ->
        three
end

identifier do (one, two) -> after three -> end
identifier do (one, two) -> after three ->; end
identifier do
  (one, two)
  ->
  after
    three
      ->
end

identifier do (one, two) -> after (three, four) -> end
identifier do (one, two) -> after (three, four) ->; end
identifier do
  (one, two)
  ->
  after
    (three, four)
    ->
end

identifier do (one, two) -> after (three, four) -> end
identifier do (one, two) -> after (three, four) ->; end
identifier do
  (one, two)
  ->
  after
    (three, four)
      ->
end

identifier do (one, two) -> after (three, key: value) -> end
identifier do (one, two) -> after (three, key: value) ->; end
identifier do
  (one, two)
  ->
  after
    (three, key: value)
      ->
end

identifier do (one, two) -> after (three, four: 4) when five -> end
identifier do (one, two) -> after (three, four: 4) when five ->; end
identifier do
  (one, two)
  ->
  after
    (three, four: 4) when five
      ->
end

identifier do (one, two) -> after (three, four: 4) when five -> six end
identifier do (one, two) -> after (three, four: 4) when five -> six; end
identifier do
  (one, two)
  ->
  after (three, four: 4) when five
    ->
      six
end

identifier do (one, two) -> after key: value -> end
identifier do (one, two) -> after key: value ->; end
identifier do
  (one, two)
  ->
  after key: value
    ->
end

identifier do (one, two) -> after three, four -> end
identifier do (one, two) -> after three, four ->; end
identifier do
  (one, two)
  ->
  after
    three, four
      ->
end

identifier do (one, two) -> after three, key: value -> end
identifier do (one, two) -> after three, key: value ->; end
identifier do
  (one, two)
  ->
  after
    three, key: value
      ->
end

identifier do (one, two) -> after three, four: 4 when five -> end
identifier do (one, two) -> after three, four: 4 when five ->; end
identifier do
  (one, two)
  ->
  after three, four: 4 when five
    ->
end

identifier do (one, two) -> after three, four: 4 when five -> six end
identifier do (one, two) -> after three, four: 4 when five -> six; end
identifier do
  (one, two)
  ->
  after three, four: 4 when five
    ->
      six
end

identifier do
  (one, two)
  ->
  after
    (three_a, four_a: 4) when five_a
      ->
        six_a
  after
    (three_b, four_b: -4) when five_b
      ->
        six_b
end

#
# 8 more variants left to the reader
#
