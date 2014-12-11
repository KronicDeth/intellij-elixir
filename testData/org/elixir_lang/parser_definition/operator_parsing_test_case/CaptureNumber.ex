# Capturing numbers have higher precedence than any matchedExpression*Operation

# should parse as &(A + (&B))
&A + &B

# should parse as &(a + (&b)
&a + &b

# should parse as (&1) + (&2)
&1 + &2
