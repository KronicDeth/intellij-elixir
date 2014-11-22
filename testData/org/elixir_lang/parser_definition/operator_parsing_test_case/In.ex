#
# inOperation(value)
#

# normal
a in b

# ...with newlines
a
in
b

# left associative
# should parse as ((a in b) in c)
a in b in c

# inOperation(atOperation(identifier))
# should parse as (@a)..(@b)
@a in @b

# inOperation(unaryOperation(identifier))
# should parse as (!a) in (~~~b)
!a in ~~~b

# inOperation(hatOperation(identifier))
# should parse as (a ^^^ b) in (c ^^^ d)
a ^^^ b in c ^^^ d

# inOperation(multiplicationOperation(identifier))
# should parse as (a * b) in (c / d)
a * b in c / d

# inOperation(additionOperation(identifier))
# should parse as (a + b) in (c - d)
a + b in c - d

# inOperation(twoOperation(identifier))
# should parse as (a..b) in (c..d)
a..b in c..d
