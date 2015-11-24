%{ &one => &two }
%{ one \\ two => three \\ four }
%{ one when one => two when two }
%{ one :: two => three :: four }
%{ one = two => three = four }
%{ one || two => three || four }
%{ one && two => three && four }
%{ one != two => three != four }
%{ one < two => three > four }
%{ one |> two => three |> four }
%{ one in two => three in four }
%{ one ++ two => three -- four }
%{ one + two => three - four }
%{ one * two => three / four }
%{ one ^^^ two => three ^^^ four }
%{ not one => !two }
%{ one.() => two.() }
%{ @one one, one: 1 => @two two, two: 2 }
%{ One.Two[1] => Three.Four[2] }
%{ One.Two => Three.Four }
%{ One.one[1] => Two.two[2] }
%{ One.one() => Two.two() }
%{ One.one => Two.two }
%{ @one[1] => @two[2] }
%{ @one => @two }
%{ one => two }
%{ @1 => @2 }
%{ &1 => &2 }
%{ !1 => !2 }
%{ fn one -> one end => fn two -> two end }
%{ (one -> one) => (two -> two) }
%{ 1 => 2 }
%{ [1] => [2] }
%{ %{ one: 1 } => %{ two: 2 } }
%{ "one" => "two" }
%{ """
   one
   """ => """
          two
          """ }
%{ 'one' => 'two' }
%{ '''
   one
   ''' => '''
          two
          ''' }
%{ ~x{one} => ~x{two} }
%{ false => nil }
%{ :one => :two }
%{ One => Two }