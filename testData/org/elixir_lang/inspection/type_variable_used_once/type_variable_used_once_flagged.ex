defmodule TypeVariableUsedOnce do
  @type single_type(<error descr="Type variable 'a' is used only once; reference it at least twice or use term()">a</error>) :: integer()
end
