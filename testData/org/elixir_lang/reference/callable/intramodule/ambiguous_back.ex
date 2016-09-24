defmodule A do
  def usage do
    referenced<caret>

    a = 1
  end

  def referenced do

  end
end
