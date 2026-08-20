defmodule DeviceLookup do
  def by_id(id), do: Repo.get(Device, id)
end

defmodule Thing do
  defp subject_tuple(%Device{} = device) do
    {DeviceLookup, :by_<caret>id, device.id}
  end
end
