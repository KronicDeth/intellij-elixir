#
# comparisonOperation(value)
#

# normal
'a' !== 'b'
'a' === 'a'
'a' != 'b'
'a' == 'a'
'a' =~ 'a'

# ...with newlines
'a'
!==
'b'

# left associative
# should parse as ('a' == 'b') !== :true
'a' == 'b' !== :true

# comparisonOperation(atOperation(value))
# should parse as (@0x1) != (@0x2)
@0x1 != @0x2

# comparisonOperation(unaryOperation(value))
# should parse as 0x1 != (~~~0x1)
0x1 != ~~~0x1

# comparisonOperation(hatOperation(value))
# should parse as (0x1 ^^^ 0x2) == (0x2 ^^^ 0x1)
0x1 ^^^ 0x2 == 0x2 ^^^ 0x1

# comparisonOperation(multiplicationOperation(value))
# should parse as (0x1 * 0x2) == (0x2 * 0x1)
0x1 * 0x2 == 0x2 * 0x1

# comparisonOperation(additionOperation(value))
# should parse as (0x1 - 0x2) != (0x2 - 0x1)
0x1 - 0x2 != 0x2 - 0x1

# comparisonOperation(twoOperation(value))
# should parse as (0x1..0x2) == (0x1..0x2)
0x1..0x2 == 0x1..0x2

# comparisonOperation(arrowOperation(value))
# should parse as ('a' |> 'b') == ('a' |> 'b')
'a' |> 'b' == 'a' |> 'b'

# comparisonOperation(relationalOperation(value))
# should parse as (0x1 < 0x2) === (0x2 > 0x1)
0x1 < 0x2 === 0x2 > 0x1
