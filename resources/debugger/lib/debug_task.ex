defmodule Mix.Tasks.IntellijElixir.DebugTask do
  @moduledoc false

  use Mix.Task

  # Functions

  @impl Mix.Task
  def run(argv) do
    {options, command_argv} = parse_args(argv)
    {task_name, task_args} = get_task(command_argv)

    # Change env if necessary, then ensure project is compiled for that environment
    change_env(task_name)
    Mix.Task.run("loadconfig")
    Mix.Task.run("compile", [])

    # Interpret project modules and project dependencies
    interpret_modules_in(Mix.Project.load_paths())
    interpret_modules_in(Mix.Project.build_path())

    # Check version, but continue anyway in case they've patched Erlang < OTP 19 to allow debugging Elixir
    version = String.to_integer(to_string(:erlang.system_info(:otp_release)))

    if version < 19 do
      IO.warn("Erlang version OTP 19 or higher required to debug Elixir. (Current version: #{version})")
    end

    # Start debugger server with the task and debugger port
    init_args = {{task_name, task_args}, Keyword.get(options, :debugger_port)}

    {:ok, pid} =
      GenServer.start_link(
        IntellijElixir.DebugServer,
        init_args,
        name: IntellijElixir.DebugServer
      )

    ref = Process.monitor(pid)

    # Wait for debugger server to finish
    receive do
      {:DOWN, ^ref, _, _, _} -> :ok
    end
  end

  ## Private Functions

  defp change_env(task) do
    if env = preferred_cli_env(task) do
      Mix.env(env)

      if project = Mix.Project.pop() do
        %{name: name, file: file} = project
        Mix.Project.push(name, file)
      end
    end
  end

  defp elixir_module_name_to_erlang_module_name(":" <> erlang_module_name), do: erlang_module_name

  defp elixir_module_name_to_erlang_module_name(erlang_module_name = "Elixir." <> _), do: erlang_module_name

  defp elixir_module_name_to_erlang_module_name(elixir_module_name), do: "Elixir." <> elixir_module_name

  defp get_task(["-" <> _ | _]) do
    Mix.shell().error(
      "** (Mix) Cannot implicitly pass flags to default Mix task, " <>
        "please invoke instead \"mix #{Mix.Project.config()[:default_task]}\""
    )

    exit({:shutdown, 1})
  end

  defp get_task([h | t]) do
    {h, t}
  end

  defp get_task([]) do
    {Mix.Project.config()[:default_task], []}
  end

  defp interpret_modules_in(paths) when is_list(paths) do
    Enum.each(paths, &interpret_modules_in/1)
  end

  defp interpret_modules_in(path) do
    reject_name_set = reject_name_set()

    path
    |> Path.join("**/*.beam")
    |> Path.wildcard()
    |> Enum.map(&Path.basename(&1, ".beam"))
    |> Enum.reject(&MapSet.member?(reject_name_set, &1))
    |> Stream.map(&String.to_atom/1)
    |> Enum.filter(&(:int.interpretable(&1) == true && !:code.is_sticky(&1) && &1 != __MODULE__))
    |> Enum.each(&:int.ni(&1))
  end

  defp parse_args(argv) do
    {debug_argv, command_argv} = Enum.split_while(argv, &(&1 != "--"))

    command_argv =
      case command_argv do
        ["--" | rest] -> rest
        _ -> command_argv
      end

    {options, _} = OptionParser.parse!(debug_argv, strict: [debugger_port: :integer])

    unless Keyword.get(options, :debugger_port) do
      Mix.shell().error("Option --debugger-port required")
      exit({:shutdown, 1})
    end

    {options, command_argv}
  end

  defp preferred_cli_env(task) do
    if System.get_env("MIX_ENV") do
      nil
    else
      task = String.to_atom(task)
      Mix.Project.config()[:preferred_cli_env][task] || Mix.Task.preferred_cli_env(task)
    end
  end

  defp reject_name_set() do
    comma_separated_elixir_module_names = System.get_env("INTELLIJ_ELIXIR_DEBUG_BLACKLIST") || ""

    comma_separated_elixir_module_names
    |> String.split(",")
    |> Enum.into(MapSet.new(), &elixir_module_name_to_erlang_module_name/1)
  end
end
