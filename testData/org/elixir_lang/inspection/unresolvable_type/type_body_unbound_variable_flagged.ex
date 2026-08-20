defmodule TypeBodyUnboundVariable do
  @type bad_free(a) :: {a, <error descr="Type 'b/0' is not defined">b</error>}
end
