defmodule Sensor do
  defp start_sensor(sensor, master_pid, sup) do
    id = [sensor["id"]]
    {:ok, pid} = Supervisor.start_child sup, worker(SensorNode, [id, master_pid], id: "sensor_#{id}")
    pid
  end
end
