defmodule ImportModule do
  import Imported, only: [imported: 0]

  <caret>imported()
end
