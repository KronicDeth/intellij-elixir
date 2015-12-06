%{ &one | &two => &three }
%{ one \\ two | three \\ four => five \\ six }
%{ one when one | two when two => three when three }
%{ one :: two | three :: four => five :: six }