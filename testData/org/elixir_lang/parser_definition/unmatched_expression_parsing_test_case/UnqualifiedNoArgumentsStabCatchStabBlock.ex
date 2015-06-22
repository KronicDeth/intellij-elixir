#
# identifier do expression catch * end
#

identifier do one catch two end
identifier do one; catch; two; end
identifier do
  one catch two
end
identifier do
  one
  catch
  two
end

identifier do one catch -> end
identifier do one catch -> ; end
identifier do
  one catch ->
end
identifier do
  one
  catch
  ->
end

identifier do one catch -> two end
identifier do one; catch -> two; end
identifier do
  one catch -> two
end
identifier do
  one
  catch
    ->
      two
end

identifier do one catch () -> two end
identifier do one; catch () -> two; end
identifier do
  one catch () -> two
end
identifier do
  one
  catch
    ()
      ->
        two
end

identifier do one catch two -> end
identifier do one catch two ->; end
identifier do
  one
  catch
    two
      ->
end

identifier do one catch (key: value) -> end
identifier do one catch (key: value) ->; end
identifier do
  one
  catch
    (key: value)
      ->
end

identifier do one catch (two, three) -> end
identifier do one catch (two, three) ->; end
identifier do
  one
  catch
    (two, three)
      ->
end

identifier do one catch (two, key: value) -> end
identifier do one catch (two, key: value) ->; end
identifier do
  one
  catch
    (two, key: value)
      ->
end

identifier do one catch (two, three: 3) when four -> end
identifier do one catch (two, three: 3) when four ->; end
identifier do
  one
  catch
    (two, three: 3) when four
      ->
end

identifier do one catch (two, three: 3) when four -> five end
identifier do one catch (two, three: 3) when four -> five; end
identifier do
  one catch (two, three: 3) when four
    ->
      five
end

identifier do one catch key: value -> end
identifier do one catch key: value ->; end
identifier do
  one catch key: value
    ->
end

identifier do one catch two, three -> end
identifier do one catch two, three ->; end
identifier do
  one
  catch
    two, three
      ->
end

identifier do one catch two, key: value -> end
identifier do one catch two, key: value ->; end
identifier do
  one
  catch
    two, key: value
      ->
end

identifier do one catch two, three: 3 when four -> end
identifier do one catch two, three: 3 when four ->; end
identifier do
  one
  catch two, three: 3 when four
    ->
end

identifier do one catch two, three: 3 when four -> five end
identifier do one catch two, three: 3 when four -> five; end
identifier do
  one catch two, three: 3 when four
    ->
      five
end

identifier do
  one
  catch
    (two_a, three_a: 3) when four_a
      ->
        five_a
  catch
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do -> catch * end
#

identifier do -> catch two end
identifier do ->; catch; two; end
identifier do
  -> catch two
end
identifier do
  ->
  catch
  two
end

identifier do -> catch -> end
identifier do -> catch -> ; end
identifier do
  -> catch ->
end
identifier do
  ->
  catch
  ->
end

identifier do -> catch -> two end
identifier do ->; catch -> two; end
identifier do
  -> catch -> two
end
identifier do
  ->
  catch
    ->
      two
end

identifier do -> catch () -> two end
identifier do ->; catch () -> two; end
identifier do
  -> catch () -> two
end
identifier do
  ->
  catch
    ()
      ->
        two
end

identifier do -> catch two -> end
identifier do -> catch two ->; end
identifier do
  ->
  catch
    two
      ->
end

identifier do -> catch (key: value) -> end
identifier do -> catch (key: value) ->; end
identifier do
  ->
  catch
    (key: value)
      ->
end

identifier do -> catch (two, three) -> end
identifier do -> catch (two, three) ->; end
identifier do
  ->
  catch
    (two, three)
      ->
end

identifier do -> catch (two, key: value) -> end
identifier do -> catch (two, key: value) ->; end
identifier do
  ->
  catch
    (two, key: value)
      ->
end

identifier do -> catch (two, three: 3) when four -> end
identifier do -> catch (two, three: 3) when four ->; end
identifier do
  ->
  catch
    (two, three: 3) when four
      ->
end

identifier do -> catch (two, three: 3) when four -> five end
identifier do -> catch (two, three: 3) when four -> five; end
identifier do
  -> catch (two, three: 3) when four
    ->
      five
end

identifier do -> catch key: value -> end
identifier do -> catch key: value ->; end
identifier do
  -> catch key: value
    ->
end

identifier do -> catch two, three -> end
identifier do -> catch two, three ->; end
identifier do
  ->
  catch
    two, three
      ->
end

identifier do -> catch two, key: value -> end
identifier do -> catch two, key: value ->; end
identifier do
  ->
  catch
    two, key: value
      ->
end

identifier do -> catch two, three: 3 when four -> end
identifier do -> catch two, three: 3 when four ->; end
identifier do
  ->
  catch two, three: 3 when four
    ->
end

identifier do -> catch two, three: 3 when four -> five end
identifier do -> catch two, three: 3 when four -> five; end
identifier do
  -> catch two, three: 3 when four
    ->
      five
end

identifier do
  ->
  catch
    (two_a, three_a: 3) when four_a
      ->
        five_a
  catch
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do -> expression catch * end
#

identifier do -> one catch two end
identifier do -> one; catch; two; end
identifier do
  -> one catch two
end
identifier do
  ->
  one
  catch
  two
end

identifier do -> one catch -> end
identifier do -> one catch -> ; end
identifier do
  -> one catch ->
end
identifier do
  ->
  one
  catch
  ->
end

identifier do -> one catch -> two end
identifier do -> one; catch -> two; end
identifier do
  -> one catch -> two
end
identifier do
  ->
  one
  catch
    ->
      two
end

identifier do -> one catch () -> two end
identifier do -> one; catch () -> two; end
identifier do
  -> one catch () -> two
end
identifier do
  ->
  one
  catch
    ()
      ->
        two
end

identifier do -> one catch two -> end
identifier do -> one catch two ->; end
identifier do
  ->
  one
  catch
    two
      ->
end

identifier do -> one catch (key: value) -> end
identifier do -> one catch (key: value) ->; end
identifier do
  ->
  one
  catch
    (key: value)
      ->
end

identifier do -> one catch (two, three) -> end
identifier do -> one catch (two, three) ->; end
identifier do
  ->
  one
  catch
    (two, three)
      ->
end

identifier do -> one catch (two, key: value) -> end
identifier do -> one catch (two, key: value) ->; end
identifier do
  ->
  one
  catch
    (two, key: value)
      ->
end

identifier do -> one catch (two, three: 3) when four -> end
identifier do -> one catch (two, three: 3) when four ->; end
identifier do
  ->
  one
  catch
    (two, three: 3) when four
      ->
end

identifier do -> one catch (two, three: 3) when four -> five end
identifier do -> one catch (two, three: 3) when four -> five; end
identifier do
  ->
  one
  catch (two, three: 3) when four
    ->
      five
end

identifier do -> one catch key: value -> end
identifier do -> one catch key: value ->; end
identifier do
  ->
  one
  catch key: value
    ->
end

identifier do -> one catch two, three -> end
identifier do -> one catch two, three ->; end
identifier do
  ->
  one
  catch
    two, three
      ->
end

identifier do -> one catch two, key: value -> end
identifier do -> one catch two, key: value ->; end
identifier do
  ->
  one
  catch
    two, key: value
      ->
end

identifier do -> one catch two, three: 3 when four -> end
identifier do -> one catch two, three: 3 when four ->; end
identifier do
  ->
  one
  catch two, three: 3 when four
    ->
end

identifier do -> one catch two, three: 3 when four -> five end
identifier do -> one catch two, three: 3 when four -> five; end
identifier do
  ->
  one
  catch two, three: 3 when four
    ->
      five
end

identifier do
  ->
  one
  catch
    (two_a, three_a: 3) when four_a
      ->
        five_a
  catch
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do () -> expression catch * end
#

identifier do () -> one catch two end
identifier do () -> one; catch; two; end
identifier do
  () -> one catch two
end
identifier do
  ()
  ->
  one
  catch
  two
end

identifier do () -> one catch -> end
identifier do () -> one catch -> ; end
identifier do
  () -> one catch ->
end
identifier do
  ()
  ->
  one
  catch
  ->
end

identifier do () -> one catch -> two end
identifier do () -> one; catch -> two; end
identifier do
  () -> one catch -> two
end
identifier do
  ()
  ->
  one
  catch
    ->
      two
end

identifier do () -> one catch () -> two end
identifier do () -> one; catch () -> two; end
identifier do
  () -> one catch () -> two
end
identifier do
  ()
  ->
  one
  catch
    ()
      ->
        two
end

identifier do () -> one catch two -> end
identifier do () -> one catch two ->; end
identifier do
  ()
  ->
  one
  catch
    two
      ->
end

identifier do () -> one catch (key: value) -> end
identifier do () -> one catch (key: value) ->; end
identifier do
  ()
  ->
  one
  catch
    (key: value)
      ->
end

identifier do () -> one catch (two, three) -> end
identifier do () -> one catch (two, three) ->; end
identifier do
  ()
  ->
  one
  catch
    (two, three)
      ->
end

identifier do () -> one catch (two, key: value) -> end
identifier do () -> one catch (two, key: value) ->; end
identifier do
  ()
  ->
  one
  catch
    (two, key: value)
      ->
end

identifier do () -> one catch (two, three: 3) when four -> end
identifier do () -> one catch (two, three: 3) when four ->; end
identifier do
  ()
  ->
  one
  catch
    (two, three: 3) when four
      ->
end

identifier do () -> one catch (two, three: 3) when four -> five end
identifier do () -> one catch (two, three: 3) when four -> five; end
identifier do
  ()
  ->
  one
  catch (two, three: 3) when four
    ->
      five
end

identifier do () -> one catch key: value -> end
identifier do () -> one catch key: value ->; end
identifier do
  ()
  ->
  one
  catch key: value
    ->
end

identifier do () -> one catch two, three -> end
identifier do () -> one catch two, three ->; end
identifier do
  ()
  ->
  one
  catch
    two, three
      ->
end

identifier do () -> one catch two, key: value -> end
identifier do () -> one catch two, key: value ->; end
identifier do
  ()
  ->
  one
  catch
    two, key: value
      ->
end

identifier do () -> one catch two, three: 3 when four -> end
identifier do () -> one catch two, three: 3 when four ->; end
identifier do
  ()
  ->
  one
  catch two, three: 3 when four
    ->
end

identifier do () -> one catch two, three: 3 when four -> five end
identifier do () -> one catch two, three: 3 when four -> five; end
identifier do
  ()
  ->
  one
  catch two, three: 3 when four
    ->
      five
end

identifier do
  ()
  ->
  one
  catch
    (two_a, three_a: 3) when four_a
      ->
        five_a
  catch
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do expression -> catch * end
#

identifier do one -> catch two end
identifier do one ->; catch; two; end
identifier do
  one -> catch two
end
identifier do
  one
  ->
  catch
  two
end

identifier do one -> catch -> end
identifier do one -> catch -> ; end
identifier do
  one -> catch ->
end
identifier do
  one
  ->
  catch
  ->
end

identifier do one -> catch -> two end
identifier do one ->; catch -> two; end
identifier do
  one -> catch -> two
end
identifier do
  one
  ->
  catch
    ->
      two
end

identifier do one -> catch () -> two end
identifier do one ->; catch () -> two; end
identifier do
  one -> catch () -> two
end
identifier do
  one
  ->
  catch
    ()
      ->
        two
end

identifier do one -> catch two -> end
identifier do one -> catch two ->; end
identifier do
  one
  ->
  catch
    two
      ->
end

identifier do one -> catch (key: value) -> end
identifier do one -> catch (key: value) ->; end
identifier do
  one
  ->
  catch
    (key: value)
      ->
end

identifier do one -> catch (two, three) -> end
identifier do one -> catch (two, three) ->; end
identifier do
  one
  ->
  catch
    (two, three)
      ->
end

identifier do one -> catch (two, key: value) -> end
identifier do one -> catch (two, key: value) ->; end
identifier do
  one
  ->
  catch
    (two, key: value)
      ->
end

identifier do one -> catch (two, three: 3) when four -> end
identifier do one -> catch (two, three: 3) when four ->; end
identifier do
  one
  ->
  catch
    (two, three: 3) when four
      ->
end

identifier do one -> catch (two, three: 3) when four -> five end
identifier do one -> catch (two, three: 3) when four -> five; end
identifier do
  one
  ->
  catch (two, three: 3) when four
    ->
      five
end

identifier do one -> catch key: value -> end
identifier do one -> catch key: value ->; end
identifier do
  one
  ->
  catch key: value
    ->
end

identifier do one -> catch two, three -> end
identifier do one -> catch two, three ->; end
identifier do
  one
  ->
  catch
    two, three
      ->
end

identifier do one -> catch two, key: value -> end
identifier do one -> catch two, key: value ->; end
identifier do
  one
  ->
  catch
    two, key: value
      ->
end

identifier do one -> catch two, three: 3 when four -> end
identifier do one -> catch two, three: 3 when four ->; end
identifier do
  one
  ->
  catch two, three: 3 when four
    ->
end

identifier do one -> catch two, three: 3 when four -> five end
identifier do one -> catch two, three: 3 when four -> five; end
identifier do
  one
  ->
  catch two, three: 3 when four
    ->
      five
end

identifier do
  one
  ->
  catch
    (two_a, three_a: 3) when four_a
      ->
        five_a
  catch
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do (key: value) -> catch * end
#

identifier do (key: value) -> catch two end
identifier do (key: value) ->; catch; two; end
identifier do
  (key: value) -> catch two
end
identifier do
  (key: value)
  ->
  catch
  two
end

identifier do (key: value) -> catch -> end
identifier do (key: value) -> catch -> ; end
identifier do
  (key: value) -> catch ->
end
identifier do
  (key: value)
  ->
  catch
  ->
end

identifier do (key: value) -> catch -> two end
identifier do (key: value) ->; catch -> two; end
identifier do
  (key: value) -> catch -> two
end
identifier do
  (key: value)
  ->
  catch
    ->
      two
end

identifier do (key: value) -> catch () -> two end
identifier do (key: value) ->; catch () -> two; end
identifier do
  (key: value) -> catch () -> two
end
identifier do
  (key: value)
  ->
  catch
    ()
      ->
        two
end

identifier do (key: value) -> catch two -> end
identifier do (key: value) -> catch two ->; end
identifier do
  (key: value)
  ->
  catch
    two
      ->
end

identifier do (key: value) -> catch (key: value) -> end
identifier do (key: value) -> catch (key: value) ->; end
identifier do
  (key: value)
  ->
  catch
    (key: value)
      ->
end

identifier do (key: value) -> catch (two, three) -> end
identifier do (key: value) -> catch (two, three) ->; end
identifier do
  (key: value)
  ->
  catch
    (two, three)
      ->
end

identifier do (key: value) -> catch (two, key: value) -> end
identifier do (key: value) -> catch (two, key: value) ->; end
identifier do
  (key: value)
  ->
  catch
    (two, key: value)
      ->
end

identifier do (key: value) -> catch (two, three: 3) when four -> end
identifier do (key: value) -> catch (two, three: 3) when four ->; end
identifier do
  (key: value)
  ->
  catch
    (two, three: 3) when four
      ->
end

identifier do (key: value) -> catch (two, three: 3) when four -> five end
identifier do (key: value) -> catch (two, three: 3) when four -> five; end
identifier do
  (key: value)
  ->
  catch (two, three: 3) when four
    ->
      five
end

identifier do (key: value) -> catch key: value -> end
identifier do (key: value) -> catch key: value ->; end
identifier do
  (key: value)
  ->
  catch key: value
    ->
end

identifier do (key: value) -> catch two, three -> end
identifier do (key: value) -> catch two, three ->; end
identifier do
  (key: value)
  ->
  catch
    two, three
      ->
end

identifier do (key: value) -> catch two, key: value -> end
identifier do (key: value) -> catch two, key: value ->; end
identifier do
  (key: value)
  ->
  catch
    two, key: value
      ->
end

identifier do (key: value) -> catch two, three: 3 when four -> end
identifier do (key: value) -> catch two, three: 3 when four ->; end
identifier do
  (key: value)
  ->
  catch two, three: 3 when four
    ->
end

identifier do (key: value) -> catch two, three: 3 when four -> five end
identifier do (key: value) -> catch two, three: 3 when four -> five; end
identifier do
  (key: value)
  ->
  catch two, three: 3 when four
    ->
      five
end

identifier do
  (key: value)
  ->
  catch
    (two_a, three_a: 3) when four_a
      ->
        five_a
  catch
    (two_b, three_b: -3) when four_b
      ->
        five_b
end

#
# identifier do (one, two) -> catch * end
#

identifier do (one, two) -> catch three end
identifier do (one, two) ->; catch; three; end
identifier do
  (one, two) -> catch three
end
identifier do
  (one, two)
  ->
  catch
  three
end

identifier do (one, two) -> catch -> end
identifier do (one, two) -> catch -> ; end
identifier do
  (one, two) -> catch ->
end
identifier do
  (one, two)
  ->
  catch
  ->
end

identifier do (one, two) -> catch -> three end
identifier do (one, two) ->; catch -> three; end
identifier do
  (one, two) -> catch -> two
end
identifier do
  (one, two)
  ->
  catch
    ->
      three
end

identifier do (one, two) -> catch () -> three end
identifier do (one, two) ->; catch () -> three; end
identifier do
  (one, two) -> catch () -> three
end
identifier do
  (one, two)
  ->
  catch
    ()
      ->
        three
end

identifier do (one, two) -> catch three -> end
identifier do (one, two) -> catch three ->; end
identifier do
  (one, two)
  ->
  catch
    three
      ->
end

identifier do (one, two) -> catch (three, four) -> end
identifier do (one, two) -> catch (three, four) ->; end
identifier do
  (one, two)
  ->
  catch
    (three, four)
    ->
end

identifier do (one, two) -> catch (three, four) -> end
identifier do (one, two) -> catch (three, four) ->; end
identifier do
  (one, two)
  ->
  catch
    (three, four)
      ->
end

identifier do (one, two) -> catch (three, key: value) -> end
identifier do (one, two) -> catch (three, key: value) ->; end
identifier do
  (one, two)
  ->
  catch
    (three, key: value)
      ->
end

identifier do (one, two) -> catch (three, four: 4) when five -> end
identifier do (one, two) -> catch (three, four: 4) when five ->; end
identifier do
  (one, two)
  ->
  catch
    (three, four: 4) when five
      ->
end

identifier do (one, two) -> catch (three, four: 4) when five -> six end
identifier do (one, two) -> catch (three, four: 4) when five -> six; end
identifier do
  (one, two)
  ->
  catch (three, four: 4) when five
    ->
      six
end

identifier do (one, two) -> catch key: value -> end
identifier do (one, two) -> catch key: value ->; end
identifier do
  (one, two)
  ->
  catch key: value
    ->
end

identifier do (one, two) -> catch three, four -> end
identifier do (one, two) -> catch three, four ->; end
identifier do
  (one, two)
  ->
  catch
    three, four
      ->
end

identifier do (one, two) -> catch three, key: value -> end
identifier do (one, two) -> catch three, key: value ->; end
identifier do
  (one, two)
  ->
  catch
    three, key: value
      ->
end

identifier do (one, two) -> catch three, four: 4 when five -> end
identifier do (one, two) -> catch three, four: 4 when five ->; end
identifier do
  (one, two)
  ->
  catch three, four: 4 when five
    ->
end

identifier do (one, two) -> catch three, four: 4 when five -> six end
identifier do (one, two) -> catch three, four: 4 when five -> six; end
identifier do
  (one, two)
  ->
  catch three, four: 4 when five
    ->
      six
end

identifier do
  (one, two)
  ->
  catch
    (three_a, four_a: 4) when five_a
      ->
        six_a
  catch
    (three_b, four_b: -4) when five_b
      ->
        six_b
end

#
# 8 more variants left to the reader
#
