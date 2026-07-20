defmodule BeamQualifiedCallFindUsages do
  def build do
    :queue.ne<caret>w()
  end

  def rebuild do
    :queue.new()
  end
end
