Process.register(self(), IntelliJElixir.Debugged)

IO.puts "Waiting for debugger to attach..."

receive do
  {:"$gen_call", from, :continue} ->
    GenServer.reply(from, :ok)
    IO.puts "... and debugger has attached."

    :ok
after
  60_000 ->
    IO.puts "... but, debugger failed to attach after 60 seconds."
    System.stop(1)
end
