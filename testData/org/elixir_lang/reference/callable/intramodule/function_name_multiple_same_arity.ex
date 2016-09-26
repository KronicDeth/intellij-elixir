defmodule A do
  def referenced() do
    referenced(true)

    a = 1
  end

  def referenced<caret>(true) do
  end

  def referenced(false) do
  end
end
