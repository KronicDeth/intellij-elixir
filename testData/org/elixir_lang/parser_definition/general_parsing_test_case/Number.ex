#
# binary
#

# current
0b01

# obsolete
0B01

#
# decimal
#

# normal
1234567890

# zero prefix
0001

# underscore separated
100_000
12_34_56_78

# trailing underscore is invalid
100_

#
# float
#

# invalid without leading integral part
.0

# invalid without trailing fractional part
0.

# valid with integral and fractional part
0.0

# valid with exponent without sign
0.0e0

# valid with exponent with sign
0.0e+1
0.0e-1
