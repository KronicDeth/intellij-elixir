{ &one, &two, &three }
{ one \\ two, three \\ four, five \\ six }
{ one :: two, three :: four, five :: six }
{ one | two, three | four, five | six }
{ one = two, three = four, five = six }
{ one || two, three or four, five ||| six }
{ one && two, three and four, five &&& six }
{ one != two, three == four, five =~ six }
{ one < two, three > four, five >= six }
{ one |> two, three <|> four, five <~ six }
{ one in two, three in four, five in six }
{ one ++ two, three -- four, five <> six }
{ one + two, three - four, five + six }
{ one * two, three / four, five * six }
{ one ^^^ two, three ^^^ four, five ^^^ six }
{ !one, not two, ^three }
{ one.(), two.(), three.() }
{ One.Two[three], Four.Five[six], Seven.Eight[nine] }
{ One.Two, Three.Four, Five.Six }
{ One.two[three], Four.five[six], Seven.eight[nine] }
{ One.two(three)(four), Five.six(seven)(eight), Nine.ten(eleven)(twelve) }
{ One.two, Three.four, Five.six }
{ @one[two], @three[four], @five[six] }
{ @one, @two, @three }
{ one(two)(three), four(five)(six), seven(eight)(nine) }
{ one[two], three[four], five[six] }
{ one, two, three }
{ @1, @2, @3 }
{ &1, &2, &3 }
{ !1, not 2, ^3 }
{ fn one -> one end, fn two -> two end, fn three -> three end }
{ ( one -> one ), (two -> two), (three -> three) }
{ ?1, ?2, ?3 }
{ 1, 2, 3 }
{ 0b1, 0b10, 0b11 }
{ 0o1, 0o2, 0o3 }
{ 0x1, 0x2, 0x3 }
{ %{ one: 1 }, %{ two | three: 3 }, %{ four => 4 } }
{ { 1 }, {2, 3}, { 4, 5, 6 } }
{ "one", "two", "three" }
{ """
  one
  """, """
       two
       """, """
            three
            """ }
{ 'one', 'two', 'three' }
{ '''
  one
  ''', '''
       two
       ''', '''
            three
            ''' }
{ ~x{one}, ~x{two}, ~x{three} }
{ false, nil, true }
{ :one, :'two', :"three" }
{ One, Two, Three }