defmodule Usage do
  def apply_sqrt do
    apply(:math, :sq<caret>rt, [4])
  end
end
