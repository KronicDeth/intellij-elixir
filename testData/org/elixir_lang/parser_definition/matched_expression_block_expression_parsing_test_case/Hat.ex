#### Unary Non-Numeric

not one ^^^ not two do end
not one ^^^ not two.(three) do end
not one ^^^ not two.(three)(four) do end
not one ^^^ not two(three) do end
not one ^^^ not two(three)(four) do end
not one ^^^ not Two.three(four) do end
not one ^^^ not Two.three(four)(five) do end
not one ^^^ not two three.(four) do end
not one ^^^ not two three.(four)(five) do end
not one ^^^ not two Three.four five do end
not one ^^^ not two Three.four five, six: 6 do end
not one ^^^ not two Three.four five, six: seven do end
not one ^^^ not two @three four do end
not one ^^^ not two @three four, five: 5 do end
not one ^^^ not two @three four, five: six do end
not one ^^^ not two Three.four(five) do end
not one ^^^ not two Three.four(five)(six) do end
not one ^^^ not two three(four) do end
not one ^^^ not two three(four)(five) do end
not one ^^^ not two three do end
not one ^^^ not two three, four: 4 do end
not one ^^^ not two three, four: five do end
not one ^^^ not two three, four do end
not one ^^^ not Two.three four, five: 5 do end
not one ^^^ not Two.three four, five: six do end
not one ^^^ not Two.three four, five do end

#~~~one ^^^ ~~~two do end
#~~~one ^^^ ~~~two.(three)(four) do end
#~~~one ^^^ ~~~two(three)(four) do end
#~~~one ^^^ ~~~two three, four: 4 do end
#
#!one ^^^ !two do end
#!one ^^^ !two.(three)(four) do end
#!one ^^^ !two(three)(four) do end
#!one ^^^ !two three, four: 4 do end
#
#^one ^^^ ^two do end
#^one ^^^ ^two.(three)(four) do end
#^one ^^^ ^two(three)(four) do end
#^one ^^^ ^two three, four: 4 do end
#
#### Dot Call
#
#one.(two)(three) ^^^ four do end
#one.(two)(three) ^^^ four.(five)(six) do end
#one.(two)(three) ^^^ four(five)(six) do end
#one.(two)(three) ^^^ four five, six: 6 do end
#
#### Bracket
#
#One.Two[three] ^^^ four do end
#One.Two[three] ^^^ four.(five)(six) do end
#One.Two[three] ^^^ four(five)(six) do end
#One.Two[three] ^^^ four five, six: 6 do end
#
#### Qualified Alias
#
#One.Two ^^^ three do end
#One.Two ^^^ three.(four)(five) do end
#One.Two ^^^ three(four)(five) do end
#One.Two ^^^ three four, five: 5 do end
#
#### Qualified Bracket
#
#One.two[three] ^^^ four do end
#One.two[three] ^^^ four.(five)(six) do end
#One.two[three] ^^^ four(five)(six) do end
#One.two[three] ^^^ four five, six: 6 do end
#
#### Qualified Parentheses call
#
#One.two(three)(four) ^^^ five do end
#One.two(three)(four) ^^^ five.(six)(seven) do end
#One.two(three)(four) ^^^ five(six)(seven) do end
#One.two(three)(four) ^^^ five six, seven: 7 do end
#
#### Qualified No Arguments call
#
#One.two ^^^ three do end
#One.two ^^^ three.(four)(five) do end
#One.two ^^^ three(four)(five) do end
#One.two ^^^ three four, five: 5 do end
#
#### At Unqualified Bracket
#
#@one[two] ^^^ three do end
#@one[two] ^^^ three.(four)(five) do end
#@one[two] ^^^ three(four)(five) do end
#@one[two] ^^^ three four, five: 5 do end
#
#### At Non-Numeric
#
#@one ^^^ @two do end
#@one ^^^ @two.(three)(four) do end
#@one ^^^ @two(three)(four) do end
#@one ^^^ @two three, four: 4 do end
#
#### Unqualified Parentheses Call
#
#one(two)(three) ^^^ four do end
#one(two)(three) ^^^ four.(five)(six) do end
#one(two)(three) ^^^ four(five)(six) do end
#one(two)(three) ^^^ four five, six: 6 do end
#
#### Unqualified Bracket
#
#one[two] ^^^ three do end
#one[two] ^^^ three.(four)(five) do end
#one[two] ^^^ three(four)(five) do end
#one[two] ^^^ three four, five: 5 do end
#
#### variable
#
#one ^^^ two do end
#one ^^^ two.(three)(four) do end
#one ^^^ two(three)(four) do end
#one ^^^ two three, four: 4 do end
#
#### access expressions
#
#### At Numeric
#
#@1 ^^^ two do end
#@1 ^^^ two.(three)(four) do end
#@1 ^^^ two(three)(four) do end
#@1 ^^^ two three, four: 4 do end
#
#### Capture Numeric
#
#&1 ^^^ two do end
#&1 ^^^ two.(three)(four) do end
#&1 ^^^ two(three)(four) do end
#&1 ^^^ two three, four: 4 do end
#
#### Unary Numeric
#
#not 1 ^^^ not two do end
#not 1 ^^^ not two.(three)(four) do end
#not 1 ^^^ not two(three)(four) do end
#not 1 ^^^ not two three, four: 4 do end
#
#~~~1 ^^^ ~~~two do end
#~~~1 ^^^ ~~~two.(three)(four) do end
#~~~1 ^^^ ~~~two(three)(four) do end
#~~~1 ^^^ ~~~two three, four: 4 do end
#
#!1 ^^^ !two do end
#!1 ^^^ !two.(three)(four) do end
#!1 ^^^ !two(three)(four) do end
#!1 ^^^ !two three, four: 4 do end
#
#^1 ^^^ ^two do end
#^1 ^^^ ^two.(three)(four) do end
#^1 ^^^ ^two(three)(four) do end
#^1 ^^^ ^two three, four: 4 do end
#
#### Anonymous function
#
#fn one end ^^^ two do end
#fn one end ^^^ two.(three)(four) do end
#fn one end ^^^ two(three)(four) do end
#fn one end ^^^ two three, four: 4 do end
#
#### Parenthetical Stab
#
#(one -> one) ^^^ two do end
#(one -> one) ^^^ two.(three)(four) do end
#(one -> one) ^^^ two(three)(four) do end
#(one -> one) ^^^ two three, four: 4 do end
#
#### Numeric
#
#1 ^^^ ^two do end
#1 ^^^ ^two.(three)(four) do end
#1 ^^^ ^two(three)(four) do end
#1 ^^^ ^two three, four: 4 do end
#
#### List
#
#[one] ^^^ two do end
#[one] ^^^ two.(three)(four) do end
#[one] ^^^ two(three)(four) do end
#[one] ^^^ two three, four: 4 do end
#
#### Map
#
#%{one: 1} ^^^ two do end
#%{one: 1} ^^^ two.(three)(four) do end
#%{one: 1} ^^^ two(three)(four) do end
#%{one: 1} ^^^ two three, four: 4 do end
#
#### Tuple
#
#{one} ^^^ two do end
#{one} ^^^ two.(three)(four) do end
#{one} ^^^ two(three)(four) do end
#{one} ^^^ two three, four: 4 do end
#
#### Bit String
#
#<< one :: tow >> ^^^ three do end
#<< one :: tow >> ^^^ three.(four)(five) do end
#<< one :: tow >> ^^^ three(four)(five) do end
#<< one :: tow >> ^^^ three four, five: 5 do end
#
#### String Line
#
#"one" ^^^ two do end
#"one" ^^^ two.(three)(four) do end
#"one" ^^^ two(three)(four) do end
#"one" ^^^ two three, four: 4 do end
#
#### String Heredoc
#
#"""
#one
#""" ^^^ two do end
#"""
#one
#""" ^^^ two.(three)(four) do end
#"""
#one
#""" ^^^ two(three)(four) do end
#"""
#one
#""" ^^^ two three, four: 4 do end
#
#### Char List Line
#
#'one' ^^^ two do end
#'one' ^^^ two.(three)(four) do end
#'one' ^^^ two(three)(four) do end
#'one' ^^^ two three, four: 4 do end
#
#### Char List Heredoc
#
#'''
#one
#''' ^^^ two do end
#'''
#one
#''' ^^^ two.(three)(four) do end
#'''
#one
#''' ^^^ two(three)(four) do end
#'''
#one
#''' ^^^ two three, four: 4 do end
#
#### Sigil
#
#~x{one}modifiers ^^^ two do end
#~x{one}modifiers ^^^ two.(three)(four) do end
#~x{one}modifiers ^^^ two(three)(four) do end
#~x{one}modifiers ^^^ two three, four: 4 do end
#
#### Atom
#
#:one ^^^ two do end
#:one ^^^ two.(three)(four) do end
#:one ^^^ two(three)(four) do end
#:one ^^^ two three, four: 4 do end
#
#### Alias
#
#One ^^^ two do end
#One ^^^ two.(three)(four) do end
#One ^^^ two(three)(four) do end
#One ^^^ two three, four: 4 do end
