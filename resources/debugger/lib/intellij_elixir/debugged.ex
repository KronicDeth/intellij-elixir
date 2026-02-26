defmodule IntelliJElixir.Debugged do
  @doc false
  def ensure_debugger_path do
    case :code.lib_dir(:debugger) do
      {:error, :bad_name} ->
        root = :code.root_dir() |> to_string()
        paths = Path.wildcard(Path.join([root, "lib", "debugger-*", "ebin"]))

        Enum.each(paths, fn path ->
          :code.add_patha(to_charlist(path))
        end)

      _ ->
        :ok
    end
  end

  def start do
    ensure_debugger_path()
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
