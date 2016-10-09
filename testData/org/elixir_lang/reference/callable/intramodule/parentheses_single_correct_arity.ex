defmodule A do
  def referenced() do
    referenced<caret>(1)

    a = 1
  end

  def referenced(_) do
  end
end
