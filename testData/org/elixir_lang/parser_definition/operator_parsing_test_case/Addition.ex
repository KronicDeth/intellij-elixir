#
# additionOperation(value)
#

# normal
0x1 + 0x2

# newline before is parsed as unary
0x1
+
0x2

# newline after is parsed as addition
0x1 +
0x2

# left associative
# +(-(0x1, 0x2), 0x3)
0x1 - 0x2 + 0x3

# +(*(0x1, 0x2), /(0x3, 0x4))
0x1 * 0x2 + 0x3 / 0x4

# +(^^^(0x1, 0x2), ^^^(0x3, 0x4))
0x1 ^^^ 0x2 + 0x3 ^^^ 0x4

# -(1, -(2))
1 - -2

# +(1, +(2))
1 + +2

# +(1, not(2))
1 + not 2

# +(1, ~~~(2))
1 + ~~~2

# +(1, !(2))
1 + !2

# +(1, ^(2)
1 + ^2

# +(.(A, B), .(C, D))
A.B + C.D

# +(.(A, b), .(C, d))
A.b + C.d

# +(@(a), @(b))
@a + @b

# +(a, b)
a + b

# +(@(?a), @(?b))
?a + ?b

# +(@(1), @(2))
@1 + @2

# +(&(?a), &(?b))
&?a + &?b

# +(&(1), &(2))
&1 + &2

# +(!(?a), ^(?b))
!?a + ^?b

# +(not(?a), ~~~(?b))
not ?a + ~~~?b

# +(!(1), ^(2))
!1 + ^2

# +(not(1), ~~~(2))
not 1 + ~~~2

# +((;), (;))
(;) + (;)

# +(?a, ?b)
?a + ?b

# +(1, 2)
1 + 2

# +([key1: ()], [key2: ()])
[key1: ()] + [key2: ()]

# +("a", "b")
"a" + "b"

# +('a', 'b')
'a' + 'b'

# +(~r{a}, ~r{b})
~r{a} + ~r{b}

# +(false, false)
false + false

# +(true, true)
true + true

# +(nil, nil)
nil + nil

# +(:a, :b)
:a + :b

# +(A, B)
A + B
