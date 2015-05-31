%{ &one | &two => &three }
%{ one \\ two | three \\ four => five \\ six }
%{ one when one: 1 | two when two: 2 => three when three: 3 }
%{ one when one | two when two => three when three }
%{ one :: two | three :: four => five :: six }