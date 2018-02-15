defmodule IntellijElixir.DebugServer do
  @moduledoc false

  use GenServer

  # Functions

  ## Client Functions

  def breakpoint_reached(pid) do
    GenServer.cast(__MODULE__, {:breakpoint_reached, pid})
  end

  ## GenServer callbacks

  @impl GenServer
  def handle_cast({:run_task}, state = %{task: {task_name, task_args}}) do
    {_pid, ref} = spawn_monitor(Mix.Task, :run, [task_name, task_args])
    {:noreply, %{state | ref: ref}}
  end

  def handle_cast({:set_breakpoint, module, line, file}, state = %{socket: socket})
      when is_atom(module) and is_integer(line) do
    unless module in :int.interpreted(), do: :int.ni(module)

    response = {:set_breakpoint_response, module, line, :int.break(module, line), file}
    send_message(socket, response)
    {:noreply, state}
  end

  def handle_cast({:remove_breakpoint, module, line}, state) when is_atom(module) and is_integer(line) do
    :int.delete_break(module, line)
    {:noreply, state}
  end

  def handle_cast({:step_into, pid}, state) when is_pid(pid) do
    :int.step(pid)
    {:noreply, state}
  end

  def handle_cast({:step_over, pid}, state) when is_pid(pid) do
    :int.next(pid)
    {:noreply, state}
  end

  def handle_cast({:step_out, pid}, state) when is_pid(pid) do
    :int.finish(pid)
    {:noreply, state}
  end

  def handle_cast({:continue, pid}, state) when is_pid(pid) do
    :int.continue(pid)
    {:noreply, state}
  end

  def handle_cast({:breakpoint_reached, pid}, state = %{socket: socket}) do
    send_message(socket, {:breakpoint_reached, pid, snapshot_with_stacks()})
    {:noreply, state}
  end

  def handle_cast(request, state) do
    super(request, state)
  end

  @impl GenServer
  def handle_info({:tcp, socket, message}, state = %{socket: socket}) do
    handle_cast(:erlang.binary_to_term(message), state)
  end

  # When task finishes, stop the server
  def handle_info({:DOWN, ref, :process, _, status}, state = %{ref: ref}) do
    {:stop, status, state}
  end

  def handle_info(request, state) do
    super(request, state)
  end

  @impl GenServer
  def init({task, port}) do
    opts = [:binary, {:packet, 4}, {:active, true}]
    {:ok, host} = :inet.gethostname()
    {:ok, socket} = :gen_tcp.connect(host, port, opts)
    :int.auto_attach([:break], {__MODULE__, :breakpoint_reached, []})

    {:ok, %{task: task, socket: socket, ref: nil}}
  end

  ## Private Functions

  defp bindings(meta_pid, level) do
    :int.meta(meta_pid, :bindings, level)
  end

  defp meta_pid_to_stack(meta_pid, %{line: break_line}) do
    [{level, mfa} | backtrace_tail] = :int.meta(meta_pid, :backtrace, :all)
    head_frame = {level, mfa, bindings(meta_pid, level), {to_file(mfa), break_line}}

    tail_frames =
      case backtrace_tail do
        # If `backtrace_tail` is empty, calling `stack_frames_above` causes an exception
        [] ->
          []

        _ ->
          frames = stack_frames_above(meta_pid, level)

          for {{level, mfa = {module, _, _}}, {level, {module, line}, bindings}} <- List.zip([backtrace_tail, frames]) do
            {level, mfa, bindings, {to_file(mfa), line}}
          end
      end

    [head_frame | tail_frames]
  end

  defp to_file(arg = {module, function, arguments}) do
    source = source(module)

    with [_] <- arguments,
         {:ok, {root, _pattern, names}} <- templates(module),
         function_string = to_string(function),
         true <- (function_string in names) do
      root_parent = root_parent(source)
      List.foldr([root_parent, root, "#{function_string}.eex"], "", &Path.join/2)
    else
      _ -> source
    end
  end

  defp pid_to_stack(pid, options) do
    case :dbg_iserver.safe_call({:get_meta, pid}) do
      {:ok, meta_pid} ->
        meta_pid_to_stack(meta_pid, options)

      error ->
        IO.warn("Failed to obtain meta pid for #{inspect(pid)}: #{inspect(error)}")

        []
    end
  end

  defp root_parent(ancestor) do
    if File.dir?(ancestor) and ancestor |> Path.join("mix.exs") |> File.exists?() do
      ancestor
    else
      ancestor
      |> Path.dirname()
      |> root_parent()
    end
  end

  defp send_message(socket, message) do
    :gen_tcp.send(socket, :erlang.term_to_binary(message))
  end

  defp snapshot_with_stacks() do
    for {pid, init, status, info} <- :int.snapshot(), into: [] do
      full_info =
        case info do
          {break_module, break_line} -> {break_module, break_line, source(break_module)}
          _ -> info
        end

      {pid, init, status, full_info, stack(pid, status, full_info)}
    end
  end

  defp source(module) do
    if Code.ensure_loaded?(module) do
      module.module_info[:compile][:source]
    end
  end

  defp stack(pid, :break, {_, break_line, _}) do
    pid_to_stack(pid, %{line: break_line})
  end

  defp stack(_, _, _), do: []

  defp stack_frames_above(meta_pid, level) do
    frame = :int.meta(meta_pid, :stack_frame, {:up, level})

    case frame do
      {next_level, _, _} -> [frame | stack_frames_above(meta_pid, next_level)]
      _ -> []
    end
  end

  defp templates(module) do
    if Code.ensure_loaded?(module) && function_exported?(module, :__templates__, 0) do
      try do
        module.__templates__()
      rescue
        _ -> :error
      else
        templates = {_root, _pattern, _names} -> {:ok, templates}
        _ -> :error
      end
    else
      :error
    end
  end
end
