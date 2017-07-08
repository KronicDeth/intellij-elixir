# map, association

%{
  one => %{two => three}
}

%{
  one => %{two: three}
}

%{
  one => %Two{three: four}
}

# map, keyword

%{
  one: %{two => three}
}

%{
  one: %{two: three}
}

%{
  one: %Two{three: four}
}

# struct

%One{
  two: %{three => four}
}

%One{
  two: %{three: four}
}

%One{
  two: %Three{four: five}
}


