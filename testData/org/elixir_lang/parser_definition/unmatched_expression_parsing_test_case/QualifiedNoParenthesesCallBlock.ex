One.Two[three].four key: value do end
One.Two[three].four five six, key: value do end
One.Two[three].four five, key: value do end
One.Two[three].four five, six do end
One.Two[three].four five + six do end

One.Two.three key: value do end
One.Two.three four five, key: value do end
One.Two.three four, key: value do end
One.Two.three four, five do end
One.Two.three four + five do end

One.two[three].four key: value do end
One.two[three].four five six, key: value do end
One.two[three].four five, key: value do end
One.two[three].four five, six do end
One.two[three].four five + six do end

One.two(three)(four).five key: value do end
One.two(three)(four).five six seven, key: value do end
One.two(three)(four).five six, key: value do end
One.two(three)(four).five six, seven do end
One.two(three)(four).five six + seven do end

One.two.three key: value do end
One.two.three four five, key: value do end
One.two.three four, key: value do end
One.two.three four, five do end
One.two.three four + five do end

@one[two].three key: value do end
@one[two].three four five, key: value do end
@one[two].three four, key: value do end
@one[two].three four, five do end
@one[two].three four + five do end

@one.two key: value do end
@one.two three four, key: value do end
@one.two three, key: value do end
@one.two three, four do end
@one.two three + four do end

one(two)(three).four key: value do end
one(two)(three).four five six, key: value do end
one(two)(three).four five, key: value do end
one(two)(three).four five, six do end
one(two)(three).four five + six do end

one[two].three key: value do end
one[two].three four five, key: value do end
one[two].three four, key: value do end
one[two].three four, five do end
one[two].three four + five do end

one.two key: value do end
one.two three four, key: value do end
one.two three, key: value do end
one.two three, four do end
one.two three + four do end

@1.two key: value do end
@1.two three four, key: value do end
@1.two three, key: value do end
@1.two three, four do end
@1.two three + four do end

&1.two key: value do end
&1.two three four, key: value do end
&1.two three, key: value do end
&1.two three, four do end
&1.two three + four do end

^1.two key: value do end
^1.two three four, key: value do end
^1.two three, key: value do end
^1.two three, four do end
^1.two three + four do end

not 1.two key: value do end
not 1.two three four, key: value do end
not 1.two three, key: value do end
not 1.two three, four do end
not 1.two three + four do end

fn -> one end.two key: value do end
fn -> one end.two three four, key: value do end
fn -> one end.two three, key: value do end
fn -> one end.two three, four do end
fn -> one end.two three + four do end

(->).one key: value do end
(->).one two three, key: value do end
(->).one two, key: value do end
(->).one two, three do end
(->).one two + three do end

1.two key: value do end
1.two three four, key: value do end
1.two three, key: value do end
1.two three, four do end
1.two three + four do end

[].one key: value do end
[].one two three, key: value do end
[].one two, key: value do end
[].one two, three do end
[].one two + three do end

%{}.one key: value do end
%{}.one two three, key: value do end
%{}.one two, key: value do end
%{}.one two, three do end
%{}.one two + three do end

{}.one key: value do end
{}.one two three, key: value do end
{}.one two, key: value do end
{}.one two, three do end
{}.one two + three do end

<<>>.one key: value do end
<<>>.one two three, key: value do end
<<>>.one two, key: value do end
<<>>.one two, three do end
<<>>.one two + three do end

"one".two key: value do end
"one".two three four, key: value do end
"one".two three, key: value do end
"one".two three, four do end
"one".two three + four do end

"""
one
""".two key: value do end
"""
one
""".two three four, key: value do end
"""
one
""".two three, key: value do end
"""
one
""".two three, four do end
"""
one
""".two three + four do end

'one'.two key: value do end
'one'.two three four, key: value do end
'one'.two three, key: value do end
'one'.two three, four do end
'one'.two three + four do end

~x{one}.two key: value do end
~x{one}.two three four, key: value do end
~x{one}.two three, key: value do end
~x{one}.two three, four do end
~x{one}.two three + four do end

false.one key: value do end
nil.one two three, key: value do end
true.one two, key: value do end
false.one two, three do end
nil.one two + three do end

:one.two key: value do end
:one.two three four, key: value do end
:one.two three, key: value do end
:one.two three, four do end
:one.two three + four do end

:one.two key: value do end
:one.two three four, key: value do end
:one.two three, key: value do end
:one.two three, four do end
:one.two three + four do end

One.two key: value do end
One.two three four, key: value do end
One.two three, key: value do end
One.two three, four do end
One.two three + four do end
