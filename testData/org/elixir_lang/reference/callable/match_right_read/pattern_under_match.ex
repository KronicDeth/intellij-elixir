defmodule PatternUnderMatch do
  def run(v) do
    result = case v do
      {:ok, x} -> <caret>x
    end
    result
  end
end
