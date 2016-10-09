defmodule A do
  def referenced do
    referenced<caret>

    a = 1
  end
end
