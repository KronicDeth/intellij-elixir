defmodule IntelliJElixir.Debugger.Server do
  @moduledoc false

  use GenServer

  require Record

  # *.hrl files are not included in all SDK installs, so need to inline definition here

  cond  do
    Version.compare(System.version(), "1.7.0") == :lt ->
      # https://github.com/elixir-lang/elixir/blob/v1.5.0/lib/elixir/src/elixir.hrl#L7-L17
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
    Version.compare(System.version(), "1.9.2") == :lt ->
      # https://github.com/elixir-lang/elixir/blob/v1.7.0/lib/elixir/src/elixir.hrl#L7-L16
      Record.defrecordp(
        :elixir_erl,
        context: nil,
        extra: nil,
        caller: false,
        vars: %{},
        backup_vars: nil,
        extra_guards: [],
        counter: %{},
        stacktrace: false
      )
    true ->
      # https://github.com/elixir-lang/elixir/blob/v1.9.2/lib/elixir/src/elixir.hrl#L8-L18
      Record.defrecordp(
        :elixir_erl,
        context: nil,
        extra: nil,
        caller: false,
        vars: %{},
        backup_vars: nil,
        extra_guards: [],
        counter: %{},
        expand_captures: false,
        stacktrace: false
      )
  end

  defstruct attached: nil,
            evaluate_meta_pid_to_froms: %{}

  # Functions

  def start_link(options = []) do
    GenServer.start_link(__MODULE__, options, name: __MODULE__)
  end

  ## Client Functions

  def breakpoint_reached(pid) do
    GenServer.cast(__MODULE__, {:breakpoint_reached, pid})
  end

  ## GenServer callbacks

  @impl GenServer
  def init([]) do
    {:ok, %__MODULE__{}}
  end

  @impl GenServer

  def handle_call(:attach, {pid, _ref}, state = %__MODULE__{attached: nil}) when is_pid(pid) do
    :int.auto_attach([:break], {__MODULE__, :breakpoint_reached, []})

    {:reply, GenServer.call(IntelliJElixir.Debugged, :continue), %__MODULE__{state | attached: pid}}
  end

  def handle_call(
        {:interpret, %{sdk_paths: sdk_paths, reject_elixir_module_name_patterns: elixir_module_name_patterns}},
        _from,
        state = %__MODULE__{}
      )
      when is_list(sdk_paths) and is_list(elixir_module_name_patterns) do
    erlang_module_name_patterns = Enum.map(elixir_module_name_patterns, &elixir_module_name_to_erlang_module_name/1)

    regex = erlang_module_name_patterns_to_regex(erlang_module_name_patterns)

    code_absolute_paths =
      :code.get_path()
      |> Stream.reject(&(&1 == '.'))
      |> Stream.map(&to_string/1)
      |> Stream.map(&Path.absname/1)
      |> Enum.sort()

    sdk_absolute_path_set =
      sdk_paths
      |> Stream.map(&Path.absname/1)
      |> MapSet.new()

    Enum.each(code_absolute_paths, fn code_absolute_path ->
      message = [:blue, "Interpreting modules under ", :magenta, code_absolute_path, :blue, "..."]

      if MapSet.member?(sdk_absolute_path_set, code_absolute_path) do
        [message, "  ...", :yellow, "skipped"]
        |> IO.ANSI.format()
        |> IO.puts()
      else
        message
        |> IO.ANSI.format()
        |> IO.puts()

        interpret_modules_in(code_absolute_path, regex)

        [:blue, "  ...", :green, "completed"]
        |> IO.ANSI.format()
        |> IO.puts()
      end
    end)

    {:reply, :ok, state}
  end

  def handle_call(:interpreted, _from, state = %__MODULE__{}) do
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

    {:reply, interpreted_modules, state}
  end

  def handle_call({:stop_interpreting, module}, _from, state = %__MODULE__{}) do
    {:reply, :int.nn(module), state}
  end

  def handle_call({:set_breakpoint, module, line}, _from, state = %__MODULE__{})
      when is_atom(module) and is_integer(line) do
    unless module in :int.interpreted(), do: :int.ni(module)

    {:reply, :int.break(module, line), state}
  end

  def handle_call({:remove_breakpoint, module, line}, _from, state) when is_atom(module) and is_integer(line) do
    {:reply, :int.delete_break(module, line), state}
  end

  def handle_call({:step_into, pid}, _from, state) when is_pid(pid) do
    {:reply, :int.step(pid), state}
  end

  def handle_call({:step_over, pid}, _from, state) when is_pid(pid) do
    {:reply, :int.next(pid), state}
  end

  def handle_call({:step_out, pid}, _from, state) when is_pid(pid) do
    {:reply, :int.finish(pid), state}
  end

  def handle_call({:continue, pid}, _from, state) when is_pid(pid) do
    {:reply, :int.continue(pid), state}
  end

  def handle_call(:stop, _from, state = %__MODULE__{}) do
    {:stop, :normal, :ok, state}
  end

  def handle_call(
        {:evaluate,
         %{
           env: %{file: file, function: function = {name, arity}, line: line, module: module},
           expression: expression,
           pid: pid,
           stack_pointer: stack_pointer
         }},
        from,
        state = %__MODULE__{}
      )
      when is_binary(file) and is_atom(name) and is_integer(arity) and arity >= 0 and is_integer(line) and
             is_atom(module) and is_binary(expression) and is_pid(pid) and is_integer(stack_pointer) do
    case :dbg_iserver.safe_call({:get_meta, pid}) do
      {:ok, meta_pid} ->
        # [IEx.Evaluator.do_eval](https://github.com/elixir-lang/elixir/blob/master/lib/iex/lib/iex/evaluator.ex#L223-L233)
        case Code.string_to_quoted(expression) do
          {:ok, quoted} ->
            elixir_variable_tuples =
              for erlang_variable_name <-
                    meta_pid
                    |> bindings(stack_pointer)
                    |> Keyword.keys(),
                  erlang_variable_name_string = to_string(erlang_variable_name),
                  named_captures =
                    Regex.named_captures(
                      ~r/(?:_|V)(?P<elixir_variable_name_string>.+)@(?<counter_string>\d)+/,
                      erlang_variable_name_string
                    ),
                  is_map(named_captures) do
                %{"elixir_variable_name_string" => elixir_variable_name_string, "counter_string" => counter_string} =
                  named_captures

                {String.to_atom(elixir_variable_name_string), String.to_integer(counter_string), erlang_variable_name}
              end
              |> Enum.group_by(fn {elixir_variable_name, _, _} -> elixir_variable_name end)
              |> Enum.map(fn {_, tuples} ->
                Enum.max_by(tuples, fn {_, counter, _} -> counter end)
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

            parsed_vars =
              Enum.map(elixir_variable_tuples, fn {elixir_variable_name, _, _} ->
                {elixir_variable_name, nil}
              end)

            parsed_scope =
              elixir_erl(
                vars: vars(elixir_variable_tuples),
                counter: counter(elixir_variable_tuples)
              )

            # https://github.com/elixir-lang/elixir/blob/8a971fcb44391bd8b16456666f3033b633c6ff77/lib/elixir/src/elixir.erl#L255
            # Elixir 1.7+ has :elixir_env.with_vars, but can't use here for Elixir 1.6.5 compatibility, so use
            # https://github.com/elixir-lang/elixir/blob/v1.6.5/lib/elixir/src/elixir.erl#L223
            vars_env = %{env | vars: parsed_vars}

            # Elixir 1.7+ uses current_vars
            current_vars_env =
              if Map.has_key?(vars_env, :current_vars) do
                %{vars_env | current_vars: Enum.into(parsed_vars, %{}, fn parsed_var -> {parsed_var, {0, :term}} end)}
              else
                vars_env
              end

            # https://github.com/elixir-lang/elixir/blob/8a971fcb44391bd8b16456666f3033b633c6ff77/lib/elixir/src/elixir.erl#L256
            {erl, _new_env, _new_scope} = quoted_to_erl(quoted, current_vars_env, parsed_scope)

            code =
              [:erl_pp.expr(erl), ?.]
              |> IO.chardata_to_string()
              |> String.to_charlist()

            :int.meta(meta_pid, :eval, {module, code, stack_pointer})

            # reply in `handle_info({_, {:eval_rsp, _}}, state)`
            {:noreply,
             update_in(state, [Access.key!(:evaluate_meta_pid_to_froms), Access.key(meta_pid, [])], &[from | &1])}

          error ->
            {:reply, error, state}
        end

      error ->
        IO.warn("Failed to obtain meta pid for #{inspect(pid)}: #{inspect(error)}")

        {:reply, error, state}
    end
  end

  @impl GenServer

  def handle_cast({:breakpoint_reached, pid}, state = %__MODULE__{attached: attached}) when is_pid(attached) do
    GenServer.cast(attached, {:breakpoint_reached, pid, snapshot_with_stacks()})

    {:noreply, state}
  end

  @impl GenServer

  def handle_info({meta_pid, {:eval_rsp, result}}, state = %__MODULE__{}) do
    {froms, new_evaluate_meta_pid_to_froms} = Map.pop(state.evaluate_meta_pid_to_froms, meta_pid)

    froms
    |> Kernel.||([])
    |> Enum.each(fn from ->
      GenServer.reply(from, result)
    end)

    {:noreply, %__MODULE__{state | evaluate_meta_pid_to_froms: new_evaluate_meta_pid_to_froms}}
  end

  @impl GenServer
  def handle_info({:DOWN, _, :process, pid, reason}, state = %__MODULE__{attached: pid}) do
    [:red, "Debugger detached: ", :red, inspect(reason)]
    |> IO.ANSI.format()
    |> IO.puts()

    {:noreply, %__MODULE__{state | attached: nil}}
  end

  ## Private Functions

  defp bindings(meta_pid, level) do
    :int.meta(meta_pid, :bindings, level)
  end

  defp counter(elixir_variable_tuples) when is_list(elixir_variable_tuples) do
    Enum.into(elixir_variable_tuples, %{}, fn {elixir_variable_name, counter, _} ->
      {elixir_variable_name, counter}
    end)
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

  defp interpret_modules_in(paths, reject_regex) when is_list(paths) do
    Enum.each(paths, &interpret_modules_in(&1, reject_regex))
  end

  defp interpret_modules_in(path, reject_regex) when is_binary(path) do
    filtered =
      path
      |> Path.join("**/*.beam")
      |> Path.wildcard()
      |> Stream.map(&Path.basename(&1, ".beam"))
      |> Enum.flat_map(fn basename ->
        if Regex.match?(reject_regex, basename) do
          []
        else
          [basename]
        end
      end)

    filtered
    |> Stream.map(&String.to_atom/1)
    |> Stream.filter(&(:int.interpretable(&1) == true && !:code.is_sticky(&1) && &1 != __MODULE__))
    |> Enum.each(&time_interpret/1)

    :ok
  end

  # `:elixir.quoted_to_erl/3` became private in Elixir 1.8, so need to inline it here.
  if Version.compare(System.version(), "1.8.0") == :lt do
    defp quoted_to_erl(quoted, env, scope) do
      :elixir.quoted_to_erl(quoted, env, scope)
    end
  else
    defp quoted_to_erl(quoted, env, scope) do
      {expanded, new_env} = :elixir_expand.expand(quoted, env)
      {erl, new_scope} = :elixir_erl_pass.translate(expanded, scope)

      {erl, new_env, new_scope}
    end
  end

  defp time_interpret(module) when is_atom(module) do
    {microseconds, result} = :timer.tc(fn -> safely_interpret(module) end)

    [[:blue, "  ", :bright, :magenta, inspect(module), :blue, " in ", :magenta, to_string(microseconds), :italic, "μs"]]
    |> IO.ANSI.format()
    |> IO.puts()

    result
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

  defp to_file({module, function, arguments}) do
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

  defp safely_interpret(module) when is_atom(module) do
    with {:module, _} <- :int.ni(module) do
      :ok
    end
  rescue
    e -> {:error, e}
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

  if Version.compare(System.version(), "1.7.0") == :lt do
    defp vars(elixir_variable_tuples) when is_list(elixir_variable_tuples) do
      Enum.into(elixir_variable_tuples, %{}, fn {elixir_variable_name, _, erlang_variable_name} ->
        # TODO determine if `0` and `true` should be different
        {{elixir_variable_name, nil}, {erlang_variable_name, 0, true}}
      end)
    end
  else
    defp vars(elixir_variable_tuples) when is_list(elixir_variable_tuples) do
      Enum.into(elixir_variable_tuples, %{}, fn {elixir_variable_name, _counter, erlang_variable_name} ->
        {{elixir_variable_name, nil}, {0, erlang_variable_name}}
      end)
    end
  end
end
