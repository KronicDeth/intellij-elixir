#
# twoOperation(value)
#

# normal
'a' ++ 'b'
'ab' -- 'b'
0x1..0x2
'a' <> 'b'

# ...with newlines
'a'
++
'b'

# right associative
# should parse as ('a' ++ ('b' ++ 'c'))
'a' ++ 'b' ++ 'c'

# twoOperation(atOperation(value))
# should parse as (@0x1)..(@0x2)
@0x1..@0x2

# twoOperation(unaryOperation(value))
# should parse as 'a' ++ (^'b')
'a' ++ ^'b'

# twoOperation(hatOperation(value))
# should parse as (^^^?a)..(^^^?z)
^^^?a..^^^?z

# twoOperation(multiplicationOperation(value))
# should parse as ('a' * 0x2) ++ ('b' * 0x3)
'a' * 0x2 ++ 'b' * 0x3

# twoOperation(additionOperation(value))
# should parse as 'a' -- (?a - ?b)
'a' -- ?a - ?b


