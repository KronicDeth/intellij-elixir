defmodule A do
  def referenced do

  end

  def usage do
    referenced<caret>

    a = 1
  end
end
