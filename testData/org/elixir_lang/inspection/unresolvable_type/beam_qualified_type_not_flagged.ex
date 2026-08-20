defmodule BeamQualifiedType do
  @type cursors :: :queue.queue()

  @spec take(cursors) :: :queue.queue()
  def take(cursors), do: cursors
end
