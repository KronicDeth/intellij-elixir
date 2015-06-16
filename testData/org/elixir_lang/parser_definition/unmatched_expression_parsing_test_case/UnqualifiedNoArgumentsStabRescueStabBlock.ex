#
# identifier do expression rescue * end
#

identifier do one rescue two end
identifier do one; rescue; two; end
identifier do
  one rescue two
end
identifier do
  one
  rescue
  two
end

identifier do one rescue -> end
identifier do one rescue -> ; end
identifier do
  one rescue ->
end
identifier do
  one
  rescue
  ->
end

identifier do one rescue -> two end
identifier do one; rescue -> two; end
identifier do
  one rescue -> two
end
identifier do
  one
  rescue
    ->
      two
end

identifier do one rescue () -> two end
identifier do one; rescue () -> two; end
identifier do
  one rescue () -> two
end
identifier do
  one
  rescue
    ()
      ->
        two
end

identifier do one rescue two -> end
identifier do one rescue two ->; end
identifier do
  one
  rescue
    two
      ->
end

identifier do one rescue (key: value) -> end
identifier do one rescue (key: value) ->; end
identifier do
  one
  rescue
    (key: value)
      ->
end

identifier do one rescue (two, three) -> end
identifier do one rescue (two, three) ->; end
identifier do
  one
  rescue
    (two, three)
      ->
end

identifier do one rescue (two, key: value) -> end
identifier do one rescue (two, key: value) ->; end
identifier do
  one
  rescue
    (two, key: value)
      ->
end

identifier do one rescue (two, three: 3) when four -> end
identifier do one rescue (two, three: 3) when four ->; end
identifier do
  one
  rescue
    (two, three: 3) when four
      ->
end

identifier do one rescue (two, three: 3) when four -> five end
identifier do one rescue (two, three: 3) when four -> five; end
identifier do
  one rescue (two, three: 3) when four
    ->
      five
end

identifier do one rescue key: value -> end
identifier do one rescue key: value ->; end
identifier do
  one rescue key: value
    ->
end

identifier do one rescue two, three -> end
identifier do one rescue two, three ->; end
identifier do
  one
  rescue
    two, three
      ->
end

identifier do one rescue two, key: value -> end
identifier do one rescue two, key: value ->; end
identifier do
  one
  rescue
    two, key: value
      ->
end

identifier do one rescue two, three: 3 when four -> end
identifier do one rescue two, three: 3 when four ->; end
identifier do
  one
  rescue two, three: 3 when four
    ->
end

identifier do one rescue two, three: 3 when four -> five end
identifier do one rescue two, three: 3 when four -> five; end
identifier do
  one rescue two, three: 3 when four
    ->
      five
end

identifier do
  one
  rescue
    (two_a, three_a: 3) when four_a
      ->
        five_a
  rescue
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do -> rescue * end
#

identifier do -> rescue two end
identifier do ->; rescue; two; end
identifier do
  -> rescue two
end
identifier do
  ->
  rescue
  two
end

identifier do -> rescue -> end
identifier do -> rescue -> ; end
identifier do
  -> rescue ->
end
identifier do
  ->
  rescue
  ->
end

identifier do -> rescue -> two end
identifier do ->; rescue -> two; end
identifier do
  -> rescue -> two
end
identifier do
  ->
  rescue
    ->
      two
end

identifier do -> rescue () -> two end
identifier do ->; rescue () -> two; end
identifier do
  -> rescue () -> two
end
identifier do
  ->
  rescue
    ()
      ->
        two
end

identifier do -> rescue two -> end
identifier do -> rescue two ->; end
identifier do
  ->
  rescue
    two
      ->
end

identifier do -> rescue (key: value) -> end
identifier do -> rescue (key: value) ->; end
identifier do
  ->
  rescue
    (key: value)
      ->
end

identifier do -> rescue (two, three) -> end
identifier do -> rescue (two, three) ->; end
identifier do
  ->
  rescue
    (two, three)
      ->
end

identifier do -> rescue (two, key: value) -> end
identifier do -> rescue (two, key: value) ->; end
identifier do
  ->
  rescue
    (two, key: value)
      ->
end

identifier do -> rescue (two, three: 3) when four -> end
identifier do -> rescue (two, three: 3) when four ->; end
identifier do
  ->
  rescue
    (two, three: 3) when four
      ->
end

identifier do -> rescue (two, three: 3) when four -> five end
identifier do -> rescue (two, three: 3) when four -> five; end
identifier do
  -> rescue (two, three: 3) when four
    ->
      five
end

identifier do -> rescue key: value -> end
identifier do -> rescue key: value ->; end
identifier do
  -> rescue key: value
    ->
end

identifier do -> rescue two, three -> end
identifier do -> rescue two, three ->; end
identifier do
  ->
  rescue
    two, three
      ->
end

identifier do -> rescue two, key: value -> end
identifier do -> rescue two, key: value ->; end
identifier do
  ->
  rescue
    two, key: value
      ->
end

identifier do -> rescue two, three: 3 when four -> end
identifier do -> rescue two, three: 3 when four ->; end
identifier do
  ->
  rescue two, three: 3 when four
    ->
end

identifier do -> rescue two, three: 3 when four -> five end
identifier do -> rescue two, three: 3 when four -> five; end
identifier do
  -> rescue two, three: 3 when four
    ->
      five
end

identifier do
  ->
  rescue
    (two_a, three_a: 3) when four_a
      ->
        five_a
  rescue
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do -> expression rescue * end
#

identifier do -> one rescue two end
identifier do -> one; rescue; two; end
identifier do
  -> one rescue two
end
identifier do
  ->
  one
  rescue
  two
end

identifier do -> one rescue -> end
identifier do -> one rescue -> ; end
identifier do
  -> one rescue ->
end
identifier do
  ->
  one
  rescue
  ->
end

identifier do -> one rescue -> two end
identifier do -> one; rescue -> two; end
identifier do
  -> one rescue -> two
end
identifier do
  ->
  one
  rescue
    ->
      two
end

identifier do -> one rescue () -> two end
identifier do -> one; rescue () -> two; end
identifier do
  -> one rescue () -> two
end
identifier do
  ->
  one
  rescue
    ()
      ->
        two
end

identifier do -> one rescue two -> end
identifier do -> one rescue two ->; end
identifier do
  ->
  one
  rescue
    two
      ->
end

identifier do -> one rescue (key: value) -> end
identifier do -> one rescue (key: value) ->; end
identifier do
  ->
  one
  rescue
    (key: value)
      ->
end

identifier do -> one rescue (two, three) -> end
identifier do -> one rescue (two, three) ->; end
identifier do
  ->
  one
  rescue
    (two, three)
      ->
end

identifier do -> one rescue (two, key: value) -> end
identifier do -> one rescue (two, key: value) ->; end
identifier do
  ->
  one
  rescue
    (two, key: value)
      ->
end

identifier do -> one rescue (two, three: 3) when four -> end
identifier do -> one rescue (two, three: 3) when four ->; end
identifier do
  ->
  one
  rescue
    (two, three: 3) when four
      ->
end

identifier do -> one rescue (two, three: 3) when four -> five end
identifier do -> one rescue (two, three: 3) when four -> five; end
identifier do
  ->
  one
  rescue (two, three: 3) when four
    ->
      five
end

identifier do -> one rescue key: value -> end
identifier do -> one rescue key: value ->; end
identifier do
  ->
  one
  rescue key: value
    ->
end

identifier do -> one rescue two, three -> end
identifier do -> one rescue two, three ->; end
identifier do
  ->
  one
  rescue
    two, three
      ->
end

identifier do -> one rescue two, key: value -> end
identifier do -> one rescue two, key: value ->; end
identifier do
  ->
  one
  rescue
    two, key: value
      ->
end

identifier do -> one rescue two, three: 3 when four -> end
identifier do -> one rescue two, three: 3 when four ->; end
identifier do
  ->
  one
  rescue two, three: 3 when four
    ->
end

identifier do -> one rescue two, three: 3 when four -> five end
identifier do -> one rescue two, three: 3 when four -> five; end
identifier do
  ->
  one
  rescue two, three: 3 when four
    ->
      five
end

identifier do
  ->
  one
  rescue
    (two_a, three_a: 3) when four_a
      ->
        five_a
  rescue
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do () -> expression rescue * end
#

identifier do () -> one rescue two end
identifier do () -> one; rescue; two; end
identifier do
  () -> one rescue two
end
identifier do
  ()
  ->
  one
  rescue
  two
end

identifier do () -> one rescue -> end
identifier do () -> one rescue -> ; end
identifier do
  () -> one rescue ->
end
identifier do
  ()
  ->
  one
  rescue
  ->
end

identifier do () -> one rescue -> two end
identifier do () -> one; rescue -> two; end
identifier do
  () -> one rescue -> two
end
identifier do
  ()
  ->
  one
  rescue
    ->
      two
end

identifier do () -> one rescue () -> two end
identifier do () -> one; rescue () -> two; end
identifier do
  () -> one rescue () -> two
end
identifier do
  ()
  ->
  one
  rescue
    ()
      ->
        two
end

identifier do () -> one rescue two -> end
identifier do () -> one rescue two ->; end
identifier do
  ()
  ->
  one
  rescue
    two
      ->
end

identifier do () -> one rescue (key: value) -> end
identifier do () -> one rescue (key: value) ->; end
identifier do
  ()
  ->
  one
  rescue
    (key: value)
      ->
end

identifier do () -> one rescue (two, three) -> end
identifier do () -> one rescue (two, three) ->; end
identifier do
  ()
  ->
  one
  rescue
    (two, three)
      ->
end

identifier do () -> one rescue (two, key: value) -> end
identifier do () -> one rescue (two, key: value) ->; end
identifier do
  ()
  ->
  one
  rescue
    (two, key: value)
      ->
end

identifier do () -> one rescue (two, three: 3) when four -> end
identifier do () -> one rescue (two, three: 3) when four ->; end
identifier do
  ()
  ->
  one
  rescue
    (two, three: 3) when four
      ->
end

identifier do () -> one rescue (two, three: 3) when four -> five end
identifier do () -> one rescue (two, three: 3) when four -> five; end
identifier do
  ()
  ->
  one
  rescue (two, three: 3) when four
    ->
      five
end

identifier do () -> one rescue key: value -> end
identifier do () -> one rescue key: value ->; end
identifier do
  ()
  ->
  one
  rescue key: value
    ->
end

identifier do () -> one rescue two, three -> end
identifier do () -> one rescue two, three ->; end
identifier do
  ()
  ->
  one
  rescue
    two, three
      ->
end

identifier do () -> one rescue two, key: value -> end
identifier do () -> one rescue two, key: value ->; end
identifier do
  ()
  ->
  one
  rescue
    two, key: value
      ->
end

identifier do () -> one rescue two, three: 3 when four -> end
identifier do () -> one rescue two, three: 3 when four ->; end
identifier do
  ()
  ->
  one
  rescue two, three: 3 when four
    ->
end

identifier do () -> one rescue two, three: 3 when four -> five end
identifier do () -> one rescue two, three: 3 when four -> five; end
identifier do
  ()
  ->
  one
  rescue two, three: 3 when four
    ->
      five
end

identifier do
  ()
  ->
  one
  rescue
    (two_a, three_a: 3) when four_a
      ->
        five_a
  rescue
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do expression -> rescue * end
#

identifier do one -> rescue two end
identifier do one ->; rescue; two; end
identifier do
  one -> rescue two
end
identifier do
  one
  ->
  rescue
  two
end

identifier do one -> rescue -> end
identifier do one -> rescue -> ; end
identifier do
  one -> rescue ->
end
identifier do
  one
  ->
  rescue
  ->
end

identifier do one -> rescue -> two end
identifier do one ->; rescue -> two; end
identifier do
  one -> rescue -> two
end
identifier do
  one
  ->
  rescue
    ->
      two
end

identifier do one -> rescue () -> two end
identifier do one ->; rescue () -> two; end
identifier do
  one -> rescue () -> two
end
identifier do
  one
  ->
  rescue
    ()
      ->
        two
end

identifier do one -> rescue two -> end
identifier do one -> rescue two ->; end
identifier do
  one
  ->
  rescue
    two
      ->
end

identifier do one -> rescue (key: value) -> end
identifier do one -> rescue (key: value) ->; end
identifier do
  one
  ->
  rescue
    (key: value)
      ->
end

identifier do one -> rescue (two, three) -> end
identifier do one -> rescue (two, three) ->; end
identifier do
  one
  ->
  rescue
    (two, three)
      ->
end

identifier do one -> rescue (two, key: value) -> end
identifier do one -> rescue (two, key: value) ->; end
identifier do
  one
  ->
  rescue
    (two, key: value)
      ->
end

identifier do one -> rescue (two, three: 3) when four -> end
identifier do one -> rescue (two, three: 3) when four ->; end
identifier do
  one
  ->
  rescue
    (two, three: 3) when four
      ->
end

identifier do one -> rescue (two, three: 3) when four -> five end
identifier do one -> rescue (two, three: 3) when four -> five; end
identifier do
  one
  ->
  rescue (two, three: 3) when four
    ->
      five
end

identifier do one -> rescue key: value -> end
identifier do one -> rescue key: value ->; end
identifier do
  one
  ->
  rescue key: value
    ->
end

identifier do one -> rescue two, three -> end
identifier do one -> rescue two, three ->; end
identifier do
  one
  ->
  rescue
    two, three
      ->
end

identifier do one -> rescue two, key: value -> end
identifier do one -> rescue two, key: value ->; end
identifier do
  one
  ->
  rescue
    two, key: value
      ->
end

identifier do one -> rescue two, three: 3 when four -> end
identifier do one -> rescue two, three: 3 when four ->; end
identifier do
  one
  ->
  rescue two, three: 3 when four
    ->
end

identifier do one -> rescue two, three: 3 when four -> five end
identifier do one -> rescue two, three: 3 when four -> five; end
identifier do
  one
  ->
  rescue two, three: 3 when four
    ->
      five
end

identifier do
  one
  ->
  rescue
    (two_a, three_a: 3) when four_a
      ->
        five_a
  rescue
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do (key: value) -> rescue * end
#

identifier do (key: value) -> rescue two end
identifier do (key: value) ->; rescue; two; end
identifier do
  (key: value) -> rescue two
end
identifier do
  (key: value)
  ->
  rescue
  two
end

identifier do (key: value) -> rescue -> end
identifier do (key: value) -> rescue -> ; end
identifier do
  (key: value) -> rescue ->
end
identifier do
  (key: value)
  ->
  rescue
  ->
end

identifier do (key: value) -> rescue -> two end
identifier do (key: value) ->; rescue -> two; end
identifier do
  (key: value) -> rescue -> two
end
identifier do
  (key: value)
  ->
  rescue
    ->
      two
end

identifier do (key: value) -> rescue () -> two end
identifier do (key: value) ->; rescue () -> two; end
identifier do
  (key: value) -> rescue () -> two
end
identifier do
  (key: value)
  ->
  rescue
    ()
      ->
        two
end

identifier do (key: value) -> rescue two -> end
identifier do (key: value) -> rescue two ->; end
identifier do
  (key: value)
  ->
  rescue
    two
      ->
end

identifier do (key: value) -> rescue (key: value) -> end
identifier do (key: value) -> rescue (key: value) ->; end
identifier do
  (key: value)
  ->
  rescue
    (key: value)
      ->
end

identifier do (key: value) -> rescue (two, three) -> end
identifier do (key: value) -> rescue (two, three) ->; end
identifier do
  (key: value)
  ->
  rescue
    (two, three)
      ->
end

identifier do (key: value) -> rescue (two, key: value) -> end
identifier do (key: value) -> rescue (two, key: value) ->; end
identifier do
  (key: value)
  ->
  rescue
    (two, key: value)
      ->
end

identifier do (key: value) -> rescue (two, three: 3) when four -> end
identifier do (key: value) -> rescue (two, three: 3) when four ->; end
identifier do
  (key: value)
  ->
  rescue
    (two, three: 3) when four
      ->
end

identifier do (key: value) -> rescue (two, three: 3) when four -> five end
identifier do (key: value) -> rescue (two, three: 3) when four -> five; end
identifier do
  (key: value)
  ->
  rescue (two, three: 3) when four
    ->
      five
end

identifier do (key: value) -> rescue key: value -> end
identifier do (key: value) -> rescue key: value ->; end
identifier do
  (key: value)
  ->
  rescue key: value
    ->
end

identifier do (key: value) -> rescue two, three -> end
identifier do (key: value) -> rescue two, three ->; end
identifier do
  (key: value)
  ->
  rescue
    two, three
      ->
end

identifier do (key: value) -> rescue two, key: value -> end
identifier do (key: value) -> rescue two, key: value ->; end
identifier do
  (key: value)
  ->
  rescue
    two, key: value
      ->
end

identifier do (key: value) -> rescue two, three: 3 when four -> end
identifier do (key: value) -> rescue two, three: 3 when four ->; end
identifier do
  (key: value)
  ->
  rescue two, three: 3 when four
    ->
end

identifier do (key: value) -> rescue two, three: 3 when four -> five end
identifier do (key: value) -> rescue two, three: 3 when four -> five; end
identifier do
  (key: value)
  ->
  rescue two, three: 3 when four
    ->
      five
end

identifier do
  (key: value)
  ->
  rescue
    (two_a, three_a: 3) when four_a
      ->
        five_a
  rescue
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do (one, two) -> rescue * end
#

identifier do (one, two) -> rescue three end
identifier do (one, two) ->; rescue; three; end
identifier do
  (one, two) -> rescue three
end
identifier do
  (one, two)
  ->
  rescue
  three
end

identifier do (one, two) -> rescue -> end
identifier do (one, two) -> rescue -> ; end
identifier do
  (one, two) -> rescue ->
end
identifier do
  (one, two)
  ->
  rescue
  ->
end

identifier do (one, two) -> rescue -> three end
identifier do (one, two) ->; rescue -> three; end
identifier do
  (one, two) -> rescue -> two
end
identifier do
  (one, two)
  ->
  rescue
    ->
      three
end

identifier do (one, two) -> rescue () -> three end
identifier do (one, two) ->; rescue () -> three; end
identifier do
  (one, two) -> rescue () -> three
end
identifier do
  (one, two)
  ->
  rescue
    ()
      ->
        three
end

identifier do (one, two) -> rescue three -> end
identifier do (one, two) -> rescue three ->; end
identifier do
  (one, two)
  ->
  rescue
    three
      ->
end

identifier do (one, two) -> rescue (three, four) -> end
identifier do (one, two) -> rescue (three, four) ->; end
identifier do
  (one, two)
  ->
  rescue
    (three, four)
    ->
end

identifier do (one, two) -> rescue (three, four) -> end
identifier do (one, two) -> rescue (three, four) ->; end
identifier do
  (one, two)
  ->
  rescue
    (three, four)
      ->
end

identifier do (one, two) -> rescue (three, key: value) -> end
identifier do (one, two) -> rescue (three, key: value) ->; end
identifier do
  (one, two)
  ->
  rescue
    (three, key: value)
      ->
end

identifier do (one, two) -> rescue (three, four: 4) when five -> end
identifier do (one, two) -> rescue (three, four: 4) when five ->; end
identifier do
  (one, two)
  ->
  rescue
    (three, four: 4) when five
      ->
end

identifier do (one, two) -> rescue (three, four: 4) when five -> six end
identifier do (one, two) -> rescue (three, four: 4) when five -> six; end
identifier do
  (one, two)
  ->
  rescue (three, four: 4) when five
    ->
      six
end

identifier do (one, two) -> rescue key: value -> end
identifier do (one, two) -> rescue key: value ->; end
identifier do
  (one, two)
  ->
  rescue key: value
    ->
end

identifier do (one, two) -> rescue three, four -> end
identifier do (one, two) -> rescue three, four ->; end
identifier do
  (one, two)
  ->
  rescue
    three, four
      ->
end

identifier do (one, two) -> rescue three, key: value -> end
identifier do (one, two) -> rescue three, key: value ->; end
identifier do
  (one, two)
  ->
  rescue
    three, key: value
      ->
end

identifier do (one, two) -> rescue three, four: 4 when five -> end
identifier do (one, two) -> rescue three, four: 4 when five ->; end
identifier do
  (one, two)
  ->
  rescue three, four: 4 when five
    ->
end

identifier do (one, two) -> rescue three, four: 4 when five -> six end
identifier do (one, two) -> rescue three, four: 4 when five -> six; end
identifier do
  (one, two)
  ->
  rescue three, four: 4 when five
    ->
      six
end

identifier do
  (one, two)
  ->
  rescue
    (three_a, four_a: 4) when five_a
      ->
        six_a
  rescue
    (three_b, four_b: -4) when five_b
      ->
        six_b
end

#
# 8 more variants left to the reader
#
