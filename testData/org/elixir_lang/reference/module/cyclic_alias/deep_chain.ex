defmodule DeepChain do
  alias MyNamespace.Referenced
  alias Referenced, as: L1
  alias L1, as: L2
  alias L2, as: L3

  L<caret>3

  @a 1
end
