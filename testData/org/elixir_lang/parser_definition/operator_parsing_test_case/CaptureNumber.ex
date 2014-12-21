# Capturing numbers have higher precedence than any *Operation

#
# Top-level
#

# &(+(A, &(B)))
&A + &B

# &(+(a, &(b)))
&a + &b

# +(&(1), &(2))
&1 + &2

#
# Nested under &
#

# &(&(+(A, &(B))))
& &A + &B

# &(&(+(a, &(b)))
& &a + &b

# &(+(&(1), &(2)))
& &1 + &2


#
# Nested under unary
#

# -(&(+(A, &(B))))
- &A + &B

# -(&(+(a, &(b))))
- &a + &b

# +(-(&(1)), &(2))
- &1 + &2

#
# Nested under @
#

# @(&(+(A, &(B))))
@&A + &B

# @(&(+(a, &(b))))
@&a + &b

# +(@(&(1)), &(2))
@&1 + &2
