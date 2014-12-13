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

# should parse as @(&1)
@&1

# should parse as @(&(a + b)))
@&a + b

# should parse as (@1) \\ (@2)
@1 \\ @2

# should parse as (@1) <- (@2)
@1 <- @2

# should parse as (@1) when (@2)
@1 when @2

# should parse as (@1) :: (@2)
@1 :: @2

# should parse as (@1) | (@2)
@1 | @2

# should parse as (@1) || (@2)
@1 || @2

# should parse as (@1) ||| (@2)
@1 ||| @2

# should parse as (@1) or (@2)
@1 or @2

# should parse as (@1) && (@2)
@1 && @2

# should parse as (@1) &&& (@2)
@1 &&& @2

# should parse as (@1) and (@2)
@1 and @2

# should parse as (@1) != (@2)
@1 != @2

# should parse as (@1) == (@2)
@1 == @2

# should parse as (@1) =~ (@2)
@1 =~ @2

# should parse as (@1) !== (@2)
@1 !== @2

# should parse as (@1) === (@2)
@1 === @2

# should parse as (@1) > (@2)
@1 > @2

# should parse as (@1) < (@2)
@1 < @2

# should parse as (@1) >= (@2)
@1 >= @2

# should parse as (@1) <= (@2)
@1 <= @2

# should parse as (@1) <~ (@2)
@1 <~ @2

# should parse as (@1) |> (@2)
@1 |> @2

# should parse as (@1) ~> (@2)
@1 ~> @2

# should parse as (@1) <<< (@2)
@1 <<< @2

# should parse as (@1) <<<~(@2)
@1 <<~ @2

# should parse as (@1) <|> (@2)
@1 <|> @2

# should parse as (@1) <~> (@2)
@1 <~> @2

# should parse as (@1) >>> (@2)
@1 >>> @2

# should parse as (@1) ~>> (@2)
@1 ~>> @2

# should parse as (@1) in (@2)
@1 in @2

# should parse as (@1) ++ (@2)
@1 ++ @2

# should parse as (@1) -- (@2)
@1 -- @2

# should parse as (@1) .. (@2)
@1 .. @2

# should parse as (@1) <> (@2)
@1 <> @2

# should parse as (@1) + (@2)
@1 + @2

# should parse as (@1) - (@2)
@1 - @2

# should parse as (@1) * (@2)
@1 * @2

# should parse as (@1) / (@2)
@1 / @2

# should parse as (@1) ^^^ (@2)
@1 ^^^ @2

# should parse as -(@1)
-@1

# should parse as +(@1)
+@1

# should parse as !(@1)
!@1

# should parse as ^(@1)
^@1

# should parse as not (@1)
not @1

# should parse as not ~~~(@1)
~~~@1

# should parse as (@One).Two
@One.Two

# should parse as (@Alias).identifier
@Alias.identifier

# should parse as (@one).two
@one.two

# should parse as @identifier
@identifier

# should parse as @1
@1

# should parse as @((;))
@(;)

# should parse as @([])
@[]

# should parse as @("string")
@"string"

# should parse as @('charList')
@'charList'

# should parse as @Alias
@Alias
