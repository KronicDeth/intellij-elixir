defmodule Mix.Tasks.IntellijElixir.DebugTask do
  @moduledoc false

  use Mix.Task

  alias IntellijElixir.DebugServer

  # Functions

  @impl Mix.Task
  def run(argv) do
    {options, command_argv} = parse_args(argv)
    {task_name, task_args} = get_task(command_argv)

    # Change env if necessary, then ensure project is compiled for that environment
    change_env(task_name)
    Mix.Task.run("loadconfig")
    Mix.Task.run("compile", [])

    elixir_module_name_patterns = elixir_module_name_patterns(options)

    state =
      DebugServer.put_reject_elixir_module_name_patterns(
        %DebugServer{
          port: Keyword.get(options, :debugger_port),
          task: {task_name, task_args}
        },
        elixir_module_name_patterns
      )

    # Interpret project modules and project dependencies
    state = interpret_modules_in(Mix.Project.load_paths(), state)
    state = interpret_modules_in(Mix.Project.build_path(), state)

    # Check version, but continue anyway in case they've patched Erlang < OTP 19 to allow debugging Elixir
    version = String.to_integer(to_string(:erlang.system_info(:otp_release)))

    if version < 19 do
      IO.warn("Erlang version OTP 19 or higher required to debug Elixir. (Current version: #{version})")
    end

    {:ok, pid} =
      GenServer.start_link(
        DebugServer,
        state,
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

  defp elixir_module_name_patterns(options) do
    environment_variables_to_elixir_module_name_patterns()
    |> Stream.concat(options_to_elixir_module_name_patterns(options))
    |> Enum.to_list()
  end

  defp environment_variables_to_elixir_module_name_patterns do
    "INTELLIJ_ELIXIR_DEBUG_BLACKLIST"
    |> System.get_env()
    |> Kernel.||("")
    |> String.split(",")
  end

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

  defp interpret_modules_in(paths, state) when is_list(paths) do
    Enum.reduce(paths, state, &interpret_modules_in/2)
  end

  defp interpret_modules_in(
         path,
         state = %DebugServer{reject_regex: reject_regex, rejected_module_names: rejected_module_names}
       ) do
    {filtered, rejected} =
      path
      |> Path.join("**/*.beam")
      |> Path.wildcard()
      |> Stream.map(&Path.basename(&1, ".beam"))
      |> Enum.reduce({[], []}, fn basename, {filter, reject} ->
        if Regex.match?(reject_regex, basename) do
          {[basename | filter], reject}
        else
          {filter, [basename | reject]}
        end
      end)

    filtered
    |> Stream.map(&String.to_atom/1)
    |> Stream.filter(&(:int.interpretable(&1) == true && !:code.is_sticky(&1) && &1 != __MODULE__))
    |> Enum.each(&:int.ni(&1))

    %DebugServer{state | rejected_module_names: rejected ++ rejected_module_names}
  end

  defp options_to_elixir_module_name_patterns(options) do
    Keyword.get_values(options, :do_not_interpret_pattern)
  end

  defp parse_args(argv) do
    {debug_argv, command_argv} = Enum.split_while(argv, &(&1 != "--"))

    command_argv =
      case command_argv do
        ["--" | rest] -> rest
        _ -> command_argv
      end

    {options, _} = OptionParser.parse!(debug_argv, strict: [debugger_port: :integer, do_not_interpret_pattern: :keep])

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
end
