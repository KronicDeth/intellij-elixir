defmodule IntelliJElixir.Debugger.Server do
  @moduledoc false

  use GenServer

  require Record

  # *.hrl files are not included in all SDK installs, so need to inline definition here
  Record.defrecordp(
    :elixir_erl,
    context: nil,
    extra: nil,
    caller: false,
    vars: %{},
    backup_vars: nil,
    export_vars: nil,
    extra_guards: [],
    counter: %{},
    file: "nofile"
  )

  defstruct reason_by_uninterpretable: %{},
            port: nil,
            reject_erlang_module_name_patterns: [],
            reject_regex: nil,
            rejected_module_names: [],
            socket: nil,
            task: nil

  # Functions

  ## Client Functions

  def breakpoint_reached(pid) do
    GenServer.cast(__MODULE__, {:breakpoint_reached, pid})
  end

  def put_reject_elixir_module_name_patterns(state = %__MODULE__{}, elixir_module_name_patterns)
      when is_list(elixir_module_name_patterns) do
    erlang_module_name_patterns = Enum.map(elixir_module_name_patterns, &elixir_module_name_to_erlang_module_name/1)
    regex = erlang_module_name_patterns_to_regex(erlang_module_name_patterns)

    %__MODULE__{state | reject_erlang_module_name_patterns: erlang_module_name_patterns, reject_regex: regex}
  end

  ## GenServer callbacks

  @impl GenServer

  def handle_cast({:interpret, module}, state = %__MODULE__{socket: socket}) when is_atom(module) do
    status =
      case :int.ni(module) do
        {:module, ^module} -> :ok
        :error = error -> error
      end

    send_message(socket, {:interpreted, module, status})

    {:noreply, state}
  end

  def handle_cast(:interpreted, state = %__MODULE__{socket: socket}) do
    interpreted_set =
      :int.interpreted()
      |> MapSet.new()

    interpreted_modules =
      :code.all_loaded()
      |> Stream.map(fn {module, _file} -> module end)
      |> Enum.sort()
      |> Enum.map(fn module ->
        {module in interpreted_set, module}
      end)

    send_message(socket, {:interpreted, interpreted_modules})

    {:noreply, state}
  end

  def handle_cast({:stop_interpreting, module}, state = %__MODULE__{socket: socket}) do
    :int.nn(module)
    send_message(socket, {:stopped_interpreting, module})
    {:noreply, state}
  end

  def handle_cast(
        :reason_by_uninterpretable,
        state = %__MODULE__{socket: socket, reason_by_uninterpretable: reason_by_uninterpretable}
      ) do
    send_message(socket, {:reason_by_uninterpretable, reason_by_uninterpretable})
    {:noreply, state}
  end

  def handle_cast(
        :rejected_module_names,
        state = %__MODULE__{rejected_module_names: rejected_module_names, socket: socket}
      ) do
    response = {:rejected_module_names, rejected_module_names}
    send_message(socket, response)
    {:noreply, state}
  end

  def handle_cast({:connect, name}, state = %__MODULE__{}) when is_atom(name) do
    true = Node.connect(name)
    # unpause debugged process waiting in `IntelliJElixir.Debugged.wait()`
    GenServer.call({IntelliJElixir.Debugged, name}, :continue)
    {:noreply, state}
  end

  def handle_cast({:set_breakpoint, module, line, file}, state = %__MODULE__{socket: socket})
      when is_binary(file)
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

  def handle_cast({:breakpoint_reached, pid}, state = %__MODULE__{socket: socket}) do
    send_message(socket, {:breakpoint_reached, pid, snapshot_with_stacks()})
    {:noreply, state}
  end

  def handle_cast(:stop, state = %__MODULE__{socket: socket}) do
    send_message(socket, :stopped)
    {:stop, :normal, state}
  end

  def handle_cast(
        {:evaluate,
         %{
           env: %{file: file, function: function = {name, arity}, line: line, module: module},
           expression: expression,
           pid: pid,
           elixir_variable_name_to_erlang_variable_name: elixir_variable_name_to_erlang_variable_name,
           stack_pointer: stack_pointer
         }},
        state = %__MODULE__{socket: socket}
      )
      when is_binary(file) and is_atom(name) and is_integer(arity) and arity >= 0 and is_integer(line) and
             is_atom(module) and is_binary(expression) and is_pid(pid) and
             is_map(elixir_variable_name_to_erlang_variable_name) and is_integer(stack_pointer) do
    case :dbg_iserver.safe_call({:get_meta, pid}) do
      {:ok, meta_pid} ->
        # [IEx.Evaluator.do_eval](https://github.com/elixir-lang/elixir/blob/master/lib/iex/lib/iex/evaluator.ex#L223-L233)
        case Code.string_to_quoted(expression) do
          {:ok, quoted} ->
            context = nil

            binding =
              Enum.map(elixir_variable_name_to_erlang_variable_name, fn {elixir_variable_name, _} ->
                {elixir_variable_name, context}
              end)

            # [IEX.Evaluator.handle_eval](https://github.com/elixir-lang/elixir/blob/master/lib/iex/lib/iex/evaluator.ex#L247-L258)
            # Can't use `:elixir.eval_forms` directly because we need to use `:int.meta(meta_pid, :eval, ...)`, so the
            # parts that get to the `erl` format

            # https://github.com/elixir-lang/elixir/blob/8a971fcb44391bd8b16456666f3033b633c6ff77/lib/elixir/src/elixir.erl#L250
            # Fake options for `env`, so that it matches debugged code's information
            env = :elixir.env_for_eval(file: file, function: function, line: line, module: module)

            # https://github.com/elixir-lang/elixir/blob/8a971fcb44391bd8b16456666f3033b633c6ff77/lib/elixir/src/elixir.erl#L254
            # `:elixir_erl_var.load_binding(binding, scope)` gives `_@#{counter}` names for all the Erlang variables,
            # ```
            # parsed_binding = [_@0: nil, _@1: nil]
            # parsed_vars = [direction: nil, string: nil]
            # parsed_scope =  {
            #   :elixir_erl,
            #   nil, # context
            #   nil, # extra
            #   false, # caller
            #   %{
            #     {:direction, nil} => {:_@0, 0, true},
            #     {:string, nil} => {:_@1, 0, true}
            #   }, # vars to aliases
            #   nil, # backup_vars
            #   nil, # export_vars
            #   [], # extra guards
            #   %{_: 2}, # counter
            #   "/Users/luke.imhoff/github/C-S-D/alembic/lib/alembic/fetch/sort.ex" # file
            # }
            # ```
            # but we know the correct erlang variable names from `elixir_variable_name_to_erlang_variable_name`, so
            # compute "parsed_vars" and "parsed_scope` manually

            parsed_vars = binding

            parsed_scope =
              elixir_erl(
                vars: vars(elixir_variable_name_to_erlang_variable_name),
                counter: counter(elixir_variable_name_to_erlang_variable_name)
              )

            # https://github.com/elixir-lang/elixir/blob/8a971fcb44391bd8b16456666f3033b633c6ff77/lib/elixir/src/elixir.erl#L255
            # Elixir 1.7+ has :elixir_env.with_vars, but can't use here for Elixir 1.6.5 compatibility, so use
            # https://github.com/elixir-lang/elixir/blob/v1.6.5/lib/elixir/src/elixir.erl#L223
            parsed_env = %{env | vars: parsed_vars}

            # https://github.com/elixir-lang/elixir/blob/8a971fcb44391bd8b16456666f3033b633c6ff77/lib/elixir/src/elixir.erl#L256
            {erl, _new_env, _new_scope} = :elixir.quoted_to_erl(quoted, parsed_env, parsed_scope)

            code =
              [:erl_pp.expr(erl), ?.]
              |> IO.chardata_to_string()
              |> String.to_charlist()

            :int.meta(meta_pid, :eval, {module, code, stack_pointer})

          error ->
            send_message(socket, {:evaluated, error})
        end

      error ->
        IO.warn("Failed to obtain meta pid for #{inspect(pid)}: #{inspect(error)}")

        send_message(socket, {:evaluated, error})
    end

    {:noreply, state}
  end

  def handle_cast(request, state) do
    super(request, state)
  end

  @impl GenServer
  def handle_info({_meta_pid, {:eval_rsp, result}}, state = %__MODULE__{socket: socket}) do
    send_message(socket, {:evaluated, result})

    {:noreply, state}
  end

  def handle_info({:tcp, socket, message}, state = %__MODULE__{socket: socket}) do
    handle_cast(:erlang.binary_to_term(message), state)
  end

  def handle_info(request, state) do
    super(request, state)
  end

  @impl GenServer
  def init(state = %__MODULE__{port: port}) do
    opts = [:binary, {:packet, 4}, {:active, true}]
    {:ok, host} = :inet.gethostname()
    {:ok, socket} = :gen_tcp.connect(host, port, opts)
    :int.auto_attach([:break], {__MODULE__, :breakpoint_reached, []})

    {:ok, %__MODULE__{state | socket: socket}}
  end

  ## Private Functions

  defp bindings(meta_pid, level) do
    :int.meta(meta_pid, :bindings, level)
  end

  defp counter(elixir_variable_name_to_erlang_variable_name)
       when is_map(elixir_variable_name_to_erlang_variable_name) do
    Enum.into(elixir_variable_name_to_erlang_variable_name, %{}, fn {elixir_variable_name, erlang_variable_name} ->
      {elixir_variable_name, counter(elixir_variable_name, erlang_variable_name)}
    end)
  end

  defp counter(elixir_variable_name, erlang_variable_name)
       when is_atom(elixir_variable_name) and is_atom(erlang_variable_name) do
    elixir_variable_name_string = to_string(elixir_variable_name)
    elixir_variable_name_string_byte_size = byte_size(elixir_variable_name_string)

    <<"V", ^elixir_variable_name_string::binary-size(elixir_variable_name_string_byte_size), "@", counter::binary>> =
      to_string(erlang_variable_name)

    String.to_integer(counter)
  end

  defp elixir_module_name_to_erlang_module_name(":" <> erlang_module_name), do: erlang_module_name

  defp elixir_module_name_to_erlang_module_name(erlang_module_name = "Elixir." <> _), do: erlang_module_name

  defp elixir_module_name_to_erlang_module_name(elixir_module_name), do: "Elixir." <> elixir_module_name

  defp erlang_module_name_pattern_to_regex_pattern(erlang_module_name_pattern) do
    erlang_module_name_pattern
    |> Regex.escape()
    |> String.replace(~S"\*", ".*")
  end

  defp erlang_module_name_patterns_to_regex(erlang_module_name_patterns) do
    unpinned_pattern =
      erlang_module_name_patterns
      |> Stream.map(&erlang_module_name_pattern_to_regex_pattern/1)
      |> Enum.join("|")

    Regex.compile!("^(#{unpinned_pattern})$")
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
         true <- function_string in names do
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

  defp vars(elixir_variable_name_to_erlang_variable_name) when is_map(elixir_variable_name_to_erlang_variable_name) do
    Enum.into(elixir_variable_name_to_erlang_variable_name, %{}, fn {elixir_variable_name, erlang_variable_name} ->
      # TODO determine if `0` and `true` should be different
      {{elixir_variable_name, nil}, {erlang_variable_name, 0, true}}
    end)
  end
end
