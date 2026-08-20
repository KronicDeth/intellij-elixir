defmodule TypeVariableUsedTwice do
  @type pair(a, b) :: {a, b}
  @type twins(a) :: {a, a}
end
