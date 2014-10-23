"""
6 hexadecimal digits
\x{123456}
\x{789012}
\x{ABCDEF}
\x{abcdef}

5 hexadecimal digits
\x{12345}
\x{67890}
\x{ABCDE}
\x{FABCD}
\x{abcde}
\x{fabcd}

4 hexadecimal digits
\x{1234}
\x{5678}
\x{9012}
\x{ABCD}
\x{EFAB}
\x{abcd}
\x{efab}

3 hexadecimal digits
\x{120}
\x{123}
\x{456}
\x{789}
\x{ABC}
\x{DEF}
\x{abc}
\x{def}

2 hexadecimal digits
\x{12}
\x{34}
\x{56}
\x{78}
\x{90}
\x{AB}
\x{CD}
\x{EF}
\x{ab}
\x{cd}
\x{ef}

1 hexadecimal digit
\x{1}
\x{2}
\x{3}
\x{4}
\x{5}
\x{6}
\x{7}
\x{8}
\x{9}
\x{0}

hexadecimal digits

2 hexadecimal digits
\x12
\x34
\x56
\x78
\x90
\xAB
\xCD
\xEF
\xab
\xcd
\xef

1 hexadecimal digit
\x1
\x2
\x3
\x4
\x5
\x6
\x7
\x8
\x9
\x0

for i <- 32..126, do: IO.puts "\\\#{<<i>>}"

\ # escaped space.  Comment needed so space isn't stripped.
\!
\"
\#
\$
\%
\&
\'
\(
\)
\*
\+
\,
\-
\.
\/
\0
\1
\2
\3
\4
\5
\6
\7
\8
\9
\:
\;
\<
\=
\>
\?
\@
\A
\B
\C
\D
\E
\F
\G
\H
\I
\J
\K
\L
\M
\N
\O
\P
\Q
\R
\S
\T
\U
\V
\W
\X
\Y
\Z
\[
\\
\]
\^
\_
\`
\a
\b
\c
\d
\e
\f
\g
\h
\i
\j
\k
\l
\m
\n
\o
\p
\q
\r
\s
\t
\u
\v
\w
\x
\y
\z
\{
\|
\}
\~
"""