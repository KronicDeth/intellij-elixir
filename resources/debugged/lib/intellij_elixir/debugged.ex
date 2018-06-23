Process.register(self(), IntelliJElixir.Debugged)

IO.puts "Waiting for debugger to attach..."

receive do
  {:"$gen_call", from, :continue} ->
    GenServer.reply(from, :ok)

    :ok
after
  60_000 ->
    IO.puts "Debugger failed to attach after 60 seconds"
    System.stop(1)
end
