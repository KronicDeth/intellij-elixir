defmodule MutualUse do
  use CycleA
  use CycleB

  Miss<caret>ing

  @a 1
end
