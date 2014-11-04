#
# atOperation(value)
#

# normal
@0x1

# ...with newlines
@
0x1

# non-associative
# should parse as (@0x1)\n(@0x2)
@0x1
@0x2
