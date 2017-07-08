not @one[two].three do end
not @one[two].three.four do end

not @one.two do end
not @one.two.three do end

not one()().two do end
not one()().two.three do end

not one[two].three do end
not one[two].three.four do end

not one.two do end
not one.two.three do end

not @1.two do end
not @1.two.three do end

not &1.two do end
not &1.two.three do end

not ^1.two do end
not ^1.two.three do end

not not 1.two do end
not not 1.two.three do end

not (->).one do end
not (->).one.two do end

not 1.two do end
not 1.two.three do end

not [].one do end
not [].one.two do end

not %{}.one do end
not %{}.one.two do end

not {}.one do end
not {}.one.two do end

not <<>>.one do end
not <<>>.one.two do end

not "one".two do end
not "one".two.three do end

not """
    one
    """.two do end
not """
    one
    """.two.three do end

not 'one'.two do end
not 'one'.two.three do end

not ~x{one}.two do end
not ~x{one}.two.three do end

not false.one do end
not false.one.two do end

not :one.two do end
not :one.two.three do end

not One.two do end
not One.two.three do end
