defmodule IntellijElixir.DebugServer do
  @moduledoc false

  use GenServer

  def init({task, port}) do
    opts = [:binary, {:packet, 4}, {:active, true}]
    {:ok, host} = :inet.gethostname()
    {:ok, socket} = :gen_tcp.connect(host, port, opts)
    :int.auto_attach([:break], {__MODULE__, :breakpoint_reached, []})

    {:ok, %{task: task, socket: socket, ref: nil}}
  end

  def breakpoint_reached(pid) do
    GenServer.cast(__MODULE__, {:breakpoint_reached, pid})
  end

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

  def handle_cast({:run_task}, state = %{task: {task_name, task_args}}) do
    {_pid, ref} = spawn_monitor(Mix.Task, :run, [task_name, task_args])
    {:noreply, %{state | ref: ref}}
  end

  def handle_cast({:set_breakpoint, module, line, file}, state = %{socket: socket}) when is_atom(module) and is_integer(line) do
    unless module in :int.interpreted, do: :int.ni(module)
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

  defp send_message(socket, message) do
    :gen_tcp.send(socket, :erlang.term_to_binary(message))
  end

  defp snapshot_with_stacks() do
    for {pid, init, status, info} <- :int.snapshot(), into: [] do
      full_info = case info do
        {break_module, break_line} -> {break_module, break_line, get_file(break_module)}
        _ -> info
      end
      {pid, init, status, full_info, get_stack(pid, status, full_info)}
    end
  end

  defp get_stack(pid, :break, full_info) do
    do_get_stackframes(pid, full_info)
  end

  defp get_stack(_, _, _) do
    []
  end

  defp do_get_stackframes(pid, {_, break_line, break_file}) do
    case(:dbg_iserver.safe_call({:get_meta, pid})) do
      {:ok, metaPid} ->
        [{level, mfa} | backtrace_rest] = :int.meta(metaPid, :backtrace, :all)
        first_frame = {level, mfa, get_bindings(metaPid, level), {break_file, break_line}}

        # If backtrace_rest is empty, calling stack_frames causes an exception
        other_frames =
          case backtrace_rest do
            [] -> []
            _ ->
              frames = stack_frames(metaPid, level)
              for {{level, mfa = {module, _, _}}, {level, {module, line}, bindings}} <- List.zip([backtrace_rest, frames]) do
                {level, mfa, bindings, {get_file(module), line}}
              end
          end

        [first_frame | other_frames]
      error ->
        :io.format('Failed to obtain meta pid for ~p: ~p~n', [pid, error])
        []
    end
  end

  defp stack_frames(metaPid, level) do
    frame = :int.meta(metaPid, :stack_frame, {:up, level})
    case frame do
      {nextLevel, _, _} -> [frame | stack_frames(metaPid, nextLevel)]
      _ -> []
    end
  end

  defp get_bindings(metaPid, sP) do
    :int.meta(metaPid, :bindings, sP)
  end

  defp get_file(module) do
    if Code.ensure_loaded?(module) do
      module.module_info
      |> Keyword.get(:compile)
      |> Keyword.get(:source)
    end
  end
end