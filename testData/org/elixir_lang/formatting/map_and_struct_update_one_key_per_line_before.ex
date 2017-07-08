# Unwrapped

## map, association

%{one => %{two | three => four}}

%{one => %{two | three: four}}

%{one => %Two{three | four: five}}

## map, keyword

%{one: %{two | three => four}}

%{one: %{two | three: four}}

%{one: %Two{three | four: five}}

## struct

%One{two: %{three | four => five}}

%One{two: %{three | four: five}}

%One{two: %Three{four | five: six}}

# Partially Wrapped

## map, association

%{
  one => %{two | three => four}
}

%{
  one => %{two | three: four}
}

%{
  one => %Two{three | four: five}
}

## map, keyword

%{
  one: %{two | three => four}
}

%{
  one: %{two | three: four}
}

%{
  one: %Two{three | four: five}
}

## struct

%One{
  two: %{three | four => five}
}

%One{
  two: %{three | four: five}
}

%One{
  two: %Three{four | five: six}
}
