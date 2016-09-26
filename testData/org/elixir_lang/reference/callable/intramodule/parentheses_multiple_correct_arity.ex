defmodule A do
  def referenced() do
    referenced<caret>(true)

    a = 1
  end

  def referenced(true) do
  end

  def referenced(false) do
  end
end
