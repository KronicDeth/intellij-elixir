y = 0

case __ENV__.line do
  x when x == true -> not x
  x when x == false -> !x
  x when x in 1..256 -> ~~~x
  x when x > 0 -> -x
  x when x < 0 -> +x
  x -> ^y = x
end