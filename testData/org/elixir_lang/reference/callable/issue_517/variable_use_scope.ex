defmodle Pooly.Server do
  def handle_cast({:checkin, worker}, %{workers: workers, monitors: monitors} = state) do
    case :ets.lookup(monitors, worker) do
      [{pid, ref}] ->
        {:noreply, %{state | workers: [pid|wo<caret>kers]}}
    end
  end
end
