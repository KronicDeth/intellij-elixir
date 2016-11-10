defmodule ImportModule do
  import Imported, except: [unimported: 0]

  <caret>imported()
end
