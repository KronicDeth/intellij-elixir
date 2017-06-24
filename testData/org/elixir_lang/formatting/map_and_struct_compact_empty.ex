# No Wrap

## map, association

%{one => %{}}

%{one => %Two{}}

## map, keyword

%{one: %{}}

%{one: %Two{}}

## struct

%One{two: %{}}

%One{two: %Three{}}

# Partial Wrap

## map, association

%{
  one => %{}
}

%{
  one => %Two{}
}

## map, keyword

%{
  one: %{}
}

%{
  one: %Two{}
}

## struct

%One{
  two: %{}
}

%One{
  two: %Three{}
}
