not One.Two[three].four key: value do end
not One.Two[three].four five six, key: value do end
not One.Two[three].four five, key: value do end
not One.Two[three].four five, six do end
not One.Two[three].four five + six do end

not One.Two.three key: value do end
not One.Two.three four five, key: value do end
not One.Two.three four, key: value do end
not One.Two.three four, five do end
not One.Two.three four + five do end

not One.two[three].four key: value do end
not One.two[three].four five six, key: value do end
not One.two[three].four five, key: value do end
not One.two[three].four five, six do end
not One.two[three].four five + six do end

not One.two(three)(four).five key: value do end
not One.two(three)(four).five six seven, key: value do end
not One.two(three)(four).five six, key: value do end
not One.two(three)(four).five six, seven do end
not One.two(three)(four).five six + seven do end

not One.two.three key: value do end
not One.two.three four five, key: value do end
not One.two.three four, key: value do end
not One.two.three four, five do end
not One.two.three four + five do end

not @one[two].three key: value do end
not @one[two].three four five, key: value do end
not @one[two].three four, key: value do end
not @one[two].three four, five do end
not @one[two].three four + five do end

not @one.two key: value do end
not @one.two three four, key: value do end
not @one.two three, key: value do end
not @one.two three, four do end
not @one.two three + four do end

not one(two)(three).four key: value do end
not one(two)(three).four five six, key: value do end
not one(two)(three).four five, key: value do end
not one(two)(three).four five, six do end
not one(two)(three).four five + six do end

not one[two].three key: value do end
not one[two].three four five, key: value do end
not one[two].three four, key: value do end
not one[two].three four, five do end
not one[two].three four + five do end

not one.two key: value do end
not one.two three four, key: value do end
not one.two three, key: value do end
not one.two three, four do end
not one.two three + four do end

not @1.two key: value do end
not @1.two three four, key: value do end
not @1.two three, key: value do end
not @1.two three, four do end
not @1.two three + four do end

not ^1.two key: value do end
not ^1.two three four, key: value do end
not ^1.two three, key: value do end
not ^1.two three, four do end
not ^1.two three + four do end

not fn -> one end.two key: value do end
not fn -> one end.two three four, key: value do end
not fn -> one end.two three, key: value do end
not fn -> one end.two three, four do end
not fn -> one end.two three + four do end

not (->).one key: value do end
not (->).one two three, key: value do end
not (->).one two, key: value do end
not (->).one two, three do end
not (->).one two + three do end

not 1.two key: value do end
not 1.two three four, key: value do end
not 1.two three, key: value do end
not 1.two three, four do end
not 1.two three + four do end

not [].one key: value do end
not [].one two three, key: value do end
not [].one two, key: value do end
not [].one two, three do end
not [].one two + three do end

not %{}.one key: value do end
not %{}.one two three, key: value do end
not %{}.one two, key: value do end
not %{}.one two, three do end
not %{}.one two + three do end

not {}.one key: value do end
not {}.one two three, key: value do end
not {}.one two, key: value do end
not {}.one two, three do end
not {}.one two + three do end

not <<>>.one key: value do end
not <<>>.one two three, key: value do end
not <<>>.one two, key: value do end
not <<>>.one two, three do end
not <<>>.one two + three do end

not "one".two key: value do end
not "one".two three four, key: value do end
not "one".two three, key: value do end
not "one".two three, four do end
not "one".two three + four do end

not """
    one
    """.two key: value do end
not """
    one
    """.two three four, key: value do end
not """
    one
    """.two three, key: value do end
not """
    one
    """.two three, four do end
not """
    one
    """.two three + four do end

not 'one'.two key: value do end
not 'one'.two three four, key: value do end
not 'one'.two three, key: value do end
not 'one'.two three, four do end
not 'one'.two three + four do end

not ~x{one}.two key: value do end
not ~x{one}.two three four, key: value do end
not ~x{one}.two three, key: value do end
not ~x{one}.two three, four do end
not ~x{one}.two three + four do end

not false.one key: value do end
not nil.one two three, key: value do end
not true.one two, key: value do end
not false.one two, three do end
not nil.one two + three do end

not :one.two key: value do end
not :one.two three four, key: value do end
not :one.two three, key: value do end
not :one.two three, four do end
not :one.two three + four do end

not :one.two key: value do end
not :one.two three four, key: value do end
not :one.two three, key: value do end
not :one.two three, four do end
not :one.two three + four do end

not One.two key: value do end
not One.two three four, key: value do end
not One.two three, key: value do end
not One.two three, four do end
not One.two three + four do end
