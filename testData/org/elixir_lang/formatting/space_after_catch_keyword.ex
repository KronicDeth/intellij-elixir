try do
  GenServer.call(:registered_name, :timeout)
catch :exit, {:timeout, _reason} ->
  {:exit, :timeout}
end
