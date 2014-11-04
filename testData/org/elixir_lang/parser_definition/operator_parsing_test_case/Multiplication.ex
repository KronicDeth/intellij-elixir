#
# multiplicationOperation(value)
#

# normal
0x1 * 0x2

# with newlines
0x1

/

0x2

# left associative
# should parse as (0x1 / 0x2) * 0x3
0x1 / 0x2 * 0x3

# multiplicationOperation(atOperation(value))
# should parse as (@0x1) * (@0x2)
@0x1 * @0x2

#
# multiplicationOperation(unaryOperation(value))
#

-0x1 * 0x2

#
# multiplicationOperation(hatOperation(unaryOperation(value))
#

~~~0x1 ^^^ -0x2 / +0x3 ^^^ ~~~0x4