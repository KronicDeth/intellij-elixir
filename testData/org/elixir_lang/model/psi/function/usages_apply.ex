defmodule ApplyTarget do
  def rev<caret>erse(list), do: list
end

defmodule ApplyTargetUsage do
  def apply_reverse do
    apply(ApplyTarget, :reverse, [[1, 2, 3]])
  end
end
