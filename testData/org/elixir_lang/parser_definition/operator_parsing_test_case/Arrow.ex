#
# arrowOperation(value)
#

# normal
'a' <<< 'b'
'a' <<~ 'b'
'a' <|> 'b'
'a' <~> 'b'
'a' >>> 'a'
'a' <~ 'b'
'a' |> 'b'
'a' ~> 'b'

# ...with newlines
'a'
|>
'b'

# left associative
# should parse as ('a' <<< ('b' >>> 'c'))
'a' <<< 'b' >>> 'c'

# arrowOperation(unaryOperation(value))
# should parse as 'a' |> (^'b')
'a' |> ^'b'

# arrowOperation(hatOperation(value))
# should parse as (0x1 ^^^ 0x2) >>> (0x3 ^^^ 0x4)
0x1 ^^^ 0x2 >>> 0x3 ^^^ 0x4

# arrowOperation(multiplicationOperation(value))
# should parse as (0x1 * 0x2) |> (0x3 * 0x4)
0x1 * 0x2 |> 0x3 * 0x4

# arrowOperation(additionOperation(value))
# should parse as (0x1 + 0x2) |> (0x3 + 0x4)
0x1 + 0x2 |> 0x3 + 0x4

# arrowOperation(twoOperation(value))
# should parse as (0x1..0x2) |> 'a'
0x1..0x2 |> 'a'
