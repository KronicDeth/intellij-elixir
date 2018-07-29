defmodule IntelliJElixir.Debugged do
  def start do
    {:ok, pid} = Supervisor.start_link([IntelliJElixir.Debugger.Server], strategy: :one_for_one)
    Process.unlink(pid)
    Process.register(self(), __MODULE__)

    [:blue, "Waiting for debugger to attach..."]
    |> IO.ANSI.format()
    |> IO.puts()

    receive do
      {:"$gen_call", from, :continue} ->
        GenServer.reply(from, :ok)

        [:green, " ...and debugger has attached."]
        |> IO.ANSI.format()
        |> IO.puts()

        {:ok, self()}

      other ->
        IO.puts(
          :stderr,
          IO.ANSI.format([:red, "... but received unexpected message (", inspect(other), ") before debugger attached."])
        )

        System.stop(1)
    end
  end
end
