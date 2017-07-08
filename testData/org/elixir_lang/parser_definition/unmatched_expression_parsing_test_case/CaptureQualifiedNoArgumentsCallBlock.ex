&@one[two].three do end
&@one[two].three.four do end

&@one.two do end
&@one.two.three do end

&one()().two do end
&one()().two.three do end

&one[two].three do end
&one[two].three.four do end

&one.two do end
&one.two.three do end

&@1.two do end
&@1.two.three do end

&^1.two do end
&^1.two.three do end

# Bugged but because it doesn't replicate bug identified by @KronicDeth in
# https://groups.google.com/d/msg/elixir-lang-core/aZ7KkHrAVzc/g2oA1-6rAQAJ
#&not 1.two do end
#&not 1.two.three do end

&fn -> one end.two do end
&fn -> one end.two.three do end

&(->).one do end
&(->).one.two do end

&1.two do end
&1.two.three do end

&[].one do end
&[].one.two do end

&%{}.one do end
&%{}.one.two do end

&{}.one do end
&{}.one.two do end

&<<>>.one do end
&<<>>.one.two do end

&"one".two do end
&"one".two.three do end

&"""
 one
 """.two do end
&"""
 one
 """.two.three do end

&'one'.two do end
&'one'.two.three do end

&~x{one}.two do end
&~x{one}.two.three do end

&false.one do end
&false.one.two do end

&:one.two do end
&:one.two.three do end

&One.two do end
&One.two.three do end
