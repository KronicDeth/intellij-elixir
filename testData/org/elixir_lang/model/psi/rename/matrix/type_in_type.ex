defmodule TypeInType do
  @type renamee :: integer
  @type wrapper :: {:w, renamee}
end
