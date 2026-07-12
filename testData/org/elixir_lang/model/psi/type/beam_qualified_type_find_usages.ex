defmodule BeamQualifiedTypeFindUsages do
  @spec take(:queue.que<caret>ue()) :: :ok
  def take(_value), do: :ok

  @spec put(:queue.queue()) :: :ok
  def put(_value), do: :ok
end
