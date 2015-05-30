%{ one = two | three = four => five = six }
%{ one || two | three || four => five || six }
%{ one && two | three && four => five && six }
%{ one != two | three != four => five == six }
%{ one < two | three > four => five <= six }
%{ one |> two | three |> four => five <<< six }
%{ one in two | three in four => five in six }
%{ one ++ two | three -- four => five <> six }
%{ one + two | three - four => five + six }
%{ one * two | three / four => five * six }
%{ one ^^^ two | three ^^^ four => five ^^^ six }
%{ not one | !two  => ^three }
%{ one.() | two.() => three.() }
%{ @one one, one: 1 | @two two, two: 2 => @three three, three: 3 }
%{ One.Two[1] | Three.Four[2] => Five.Six[3] }
%{ One.Two | Three.Four => Five.Six }
%{ One.one[1] | Two.two[2] => Three.three[3] }
%{ One.one() | Two.two() => Three.three() }
%{ One.one | Two.two => Three.three }
%{ @one[1] | @two[2] => @three[3] }
%{ @one | @two => @three }
%{ one | two => three }
%{ @1 | @2 => @3 }
%{ &1 | &2 => &3 }
%{ !1 | !2 => !3 }
%{ fn one -> one end | fn two -> two end => fn three -> three end }
%{ (one -> one) | (two -> two) => (three -> three) }
%{ 1 | 2 => 3 }
%{ [1] | [2] => [3] }
%{ %{ one: 1 } | %{ two: 2 } => %{ three: 3 } }
%{ "one" | "two" => "three" }
%{ """
   one
   """ | """
         two
         """ => """
                three
                """ }
%{ 'one' | 'two' => 'three' }
%{ '''
   one
   ''' | '''
         two
         ''' => '''
                three
                ''' }
%{ ~x{one} | ~x{two} => ~x{three} }
%{ false | nil => true }
%{ :one | :two => :three }
%{ One | Two => Three }