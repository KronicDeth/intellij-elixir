#
# captureOperation(value)
#

# normal
&:foo

# ...with newlines
&
:foo

# non-associative
# should parse as ((&:foo)\n(&:bar))
&:foo
&:bar

# associationOperation(atOperation(value))
# should parse as &(@0x1)
&@0x1

# captureOperation(unaryOperation(value))
# should parse as &(~~~0x1)
&~~~:foo

# captureOperation(hatOperation(value))
# should parse as &(0x2 ^^^ 0x1)
&0x2 ^^^ 0x1

# captureOperation(multiplicationOperation(value))
# should parse as &(0x2 * 0x1)
&0x2 * 0x1

# captureOperation(additionOperation(value))
# should parse as &(0x1 + 0x2)
&0x1 + 0x2

# captureOperation(twoOperation(value))
# should parse as &(0x3..0x4)
&0x3..0x4

# captureOperation(inOperation(identifier))
# should parse as &(a in b)
&a in b

# captureOperation(arrowOperation(value))
# should parse as &('c' |> 'd')
&'c' |> 'd'

# captureOperation(relationalOperation(value))
# should parse as &(0x2 > 0x1)
&0x2 > 0x1

# captureOperation(comparisonOperation(value))
# should parse as &(0x1 == 0x1)
&0x1 == 0x1

# captureOperation(andOperation(value))
# should parse as &(0x2 &&& 0x1)
&0x2 &&& 0x1

# captureOperation(orOperation(value))
# should parse as &(0x2 ||| 0x1)
&0x2 ||| 0x1

# captureOperation(matchOperation(value))
# should parse as &(0x2 = 0x2)
&0x2 = 0x2

# captureOperation(pipeOperation(value))
# should parse as &(0x3 | 0x4)
&0x3 | 0x4

# captureOperation(typeOperation(value))
# should parse as &(0x3 :: 0x4)
&0x3 :: 0x4

# captureOperation(whenOperation(value))
# should parse as &(0x3 when 0x4)
&0x3 when 0x4

# captureOperation(inMatchOperation(value))
# should parse as &(0x3 <- 0x4)
&0x3 <- 0x4

#
# captureOptions(noParenthesesExpression)
#

# should parse as &(function(positional, key: value))
&function positional, key: value

# should parse as &(function(first, second))
&function first, second

# should parse as &(function(first, second, key: value))
&function first, second, key: value

# captureOperation(associationOperation(value))
# should parse as &(0x3 => 0x4)
&0x3 => 0x4
