# Source code recreated from a .beam file by IntelliJ Elixir
defmodule :fprof do

  # Functions

  def unquote(:"$code_change")(state) do
    case :lists.keysearch(:time, 1, module_info(:compile)) do
      {:value, {:time, {y, m, d, hH, mM, sS}}} ->
        :io.format('~n~w: code change to compile time ' ++ '~4..0w-~2..0w-~2..0w ~2..0w:~2..0w:~2..0w~n', [:fprof, y, m, d, hH, mM, sS])
      false ->
        :ok
    end
    server_loop(state)
  end

  def analyse(), do: analyse([])

  def analyse(option) when is_atom(option), do: analyse([option])

  def analyse({opt, _Val} = option) when is_atom(opt), do: analyse([option])

  def analyse(options) when is_list(options), do: ...

  def analyse(options), do: :erlang.error(:badarg, [options])

  def analyse(option, value) when is_atom(option), do: analyse([{option, value}])

  def analyse(option, value), do: :erlang.error(:badarg, [option, value])

  def apply({m, f}, args) when is_atom(m) and is_atom(f) and is_list(args), do: apply_1(m, f, args, [])

  def apply(fun, args) when is_function(fun) and is_list(args), do: apply_1(fun, args, [])

  def apply(a, b), do: :erlang.error(:badarg, [a, b])

  def apply(m, f, args) when is_atom(m) and is_atom(f) and is_list(args), do: apply_1(m, f, args, [])

  def apply({m, f}, args, options) when is_atom(m) and is_atom(f) and is_list(args) and is_list(options), do: apply_1(m, f, args, options)

  def apply(fun, args, options) when is_function(fun) and is_list(args) and is_list(options), do: apply_1(fun, args, options)

  def apply(a, b, c), do: :erlang.error(:badarg, [a, b, c])

  def apply(m, f, args, options) when is_atom(m) and is_atom(f) and is_list(args) and is_list(options), do: apply_1(m, f, args, options)

  def apply(a, b, c, d), do: :erlang.error(:badarg, [a, b, c, d])

  def call(request) do
    case whereis(:fprof_server) do
      :undefined ->
        start()
        just_call(request)
      server ->
        just_call(server, request)
    end
  end

  def code_change(), do: just_call(:"$code_change")

  def get_state(), do: just_call(get_state())

  def getopts(list, options) when is_list(list) and is_list(options), do: getopts_1(options, list, [])

  def just_call(request), do: just_call(whereis(:fprof_server), request)

  def load_profile(), do: load_profile([])

  def load_profile(option) when is_atom(option), do: load_profile([option])

  def load_profile(options) when is_list(options) do
    case getopts(options, [:file]) do
      {[file], []} ->
        call(load_profile(file: case file do
          [] ->
            'fprof.profile'
          [{:file, f}] ->
            f
          _ ->
            :erlang.error(:badarg, [options])
        end))
      _ ->
        :erlang.error(:badarg, [options])
    end
  end

  def load_profile(options), do: :erlang.error(:badarg, [options])

  def load_profile(option, value) when is_atom(option), do: load_profile([{option, value}])

  def load_profile(option, value), do: :erlang.error(:badarg, [option, value])

  def module_info() do
    # body not decompiled
  end

  def module_info(p0) do
    # body not decompiled
  end

  def parsify([]), do: []

  def parsify([hd | tl]), do: [parsify(hd) | parsify(tl)]

  def parsify({a, b}), do: {parsify(a), parsify(b)}

  def parsify({a, b, c}), do: {parsify(a), parsify(b), parsify(c)}

  def parsify(tuple) when is_tuple(tuple), do: list_to_tuple(parsify(tuple_to_list(tuple)))

  def parsify(pid) when is_pid(pid), do: :erlang.pid_to_list(pid)

  def parsify(port) when is_port(port), do: :erlang.port_to_list(port)

  def parsify(ref) when is_reference(ref), do: :erlang.ref_to_list(ref)

  def parsify(fun) when is_function(fun), do: :erlang.fun_to_list(fun)

  def parsify(term), do: term

  def print_called(dest, []), do: println(dest, ' [', [], ']}.', "")

  def print_called(dest, [clocks]), do: println(dest, ' [{', clocks, '}]}.', "")

  def print_called(dest, [clocks | tail]) do
    println(dest, ' [{', clocks, '},', "")
    print_called_1(dest, tail)
  end

  def print_callers(dest, []), do: println(dest, '{[', [], '],', "")

  def print_callers(dest, [clocks]), do: println(dest, '{[{', clocks, '}],', "")

  def print_callers(dest, [clocks | tail]) do
    println(dest, '{[{', clocks, '},', "")
    print_callers_1(dest, tail)
  end

  def print_func(dest, clocks), do: println(dest, ' { ', clocks, '},', '%')

  def println({:undefined, _}, _Head, _, _Tail, _Comment), do: :ok

  def println({io, [w1, w2, w3, w4]}, head, clocks(id: pid, cnt: cnt, acc: _, own: own), tail, comment) when is_pid(pid), do: :io.put_chars(io, [pad(head, ?\s, 3), flat_format(parsify(pid), ?,, w1), flat_format(cnt, ?,, w2, :right), flat_format(:undefined, ?,, w3, :right), flat_format(own * 0.001, [], w4 - 1, :right), pad(tail, ?\s, 4), pad(?\s, comment, 4), :io_lib.nl()])

  def println({io, [w1, w2, w3, w4]}, head, clocks(id: {_M, _F, _A} = func, cnt: cnt, acc: acc, own: own), tail, comment), do: :io.put_chars(io, [pad(head, ?\s, 3), flat_format(func, ?,, w1), flat_format(cnt, ?,, w2, :right), flat_format(acc * 0.001, ?,, w3, :right), flat_format(own * 0.001, [], w4 - 1, :right), pad(tail, ?\s, 4), pad(?\s, comment, 4), :io_lib.nl()])

  def println({io, [w1, w2, w3, w4]}, head, clocks(id: id, cnt: cnt, acc: acc, own: own), tail, comment), do: :io.put_chars(io, [pad(head, ?\s, 3), flat_format(parsify(id), ?,, w1), flat_format(cnt, ?,, w2, :right), flat_format(acc * 0.001, ?,, w3, :right), flat_format(own * 0.001, [], w4 - 1, :right), pad(tail, ?\s, 4), pad(?\s, comment, 4), :io_lib.nl()])

  def println({io, [w1, w2, w3, w4]}, head, :head, tail, comment), do: :io.put_chars(io, [pad(head, ?\s, 3), pad(' ', ?\s, w1), pad(?\s, ' CNT ', w2), pad(?\s, ' ACC ', w3), pad(?\s, ' OWN', w4 - 1), pad(tail, ?\s, 4), pad(?\s, comment, 4), :io_lib.nl()])

  def println({io, _}, head, [], tail, comment), do: :io.format(io, '~s~ts~ts~n', [pad(head, ?\s, 3), tail, comment])

  def println({io, _}, head, {tag, term}, tail, comment), do: :io.format(io, '~s~tp, ~tp~ts~ts~n', [pad(head, ?\s, 3), parsify(tag), parsify(term), tail, comment])

  def println({io, _}, head, term, tail, comment), do: :io.format(io, '~s~tp~ts~ts~n', [pad(head, ?\s, 3), parsify(term), tail, comment])

  def profile(), do: profile([])

  def profile(option) when is_atom(option), do: profile([option])

  def profile({opt, _Val} = option) when is_atom(opt), do: profile([option])

  def profile(options) when is_list(options), do: ...

  def profile(options), do: :erlang.error(:badarg, [options])

  def profile(option, value) when is_atom(option), do: profile([{option, value}])

  def profile(option, value), do: :erlang.error(:badarg, [option, value])

  def reply({mref, pid}, reply) when is_reference(mref) and is_pid(pid) do
    try do
      send(pid, {:fprof_server, mref, reply})
    catch
      error -> error
    end
    :ok
  end

  def save_profile(), do: save_profile([])

  def save_profile(option) when is_atom(option), do: save_profile([option])

  def save_profile(options) when is_list(options) do
    case getopts(options, [:file]) do
      {[file], []} ->
        call(save_profile(file: case file do
          [] ->
            'fprof.profile'
          [{:file, f}] ->
            f
          _ ->
            :erlang.error(:badarg, [options])
        end))
      _ ->
        :erlang.error(:badarg, [options])
    end
  end

  def save_profile(options), do: :erlang.error(:badarg, [options])

  def save_profile(option, value) when is_atom(option), do: save_profile([{option, value}])

  def save_profile(option, value), do: :erlang.error(:badarg, [option, value])

  def setopts(options) when is_list(options), do: :lists.append(options)

  def start() do
    spawn_3step(fn  ->
        try do
          register(:fprof_server, self())
        catch
          {:error, :badarg, _} ->
            {{:error, {:already_started, whereis(:fprof_server)}}, :already_started}
        else
          true ->
            process_flag(:trap_exit, true)
            {{:ok, self()}, :loop}
        end
    end, fn x ->
        x
    end, fn :loop ->
        put(:trace_state, :idle)
        put(:profile_state, {:idle, :undefined})
        put(:pending_stop, [])
        server_loop([])
      :already_started ->
        :ok
    end)
  end

  def stop(), do: stop(:normal)

  def stop(:kill) do
    case whereis(:fprof_server) do
      :undefined ->
        :ok
      pid ->
        exit(pid, :kill)
        :ok
    end
  end

  def stop(reason) do
    just_call(stop(reason: reason))
    :ok
  end

  def trace(:stop), do: call(trace_stop())

  def trace(:verbose), do: trace([:start, :verbose])

  def trace([:stop]), do: call(trace_stop())

  def trace({opt, _Val} = option) when is_atom(opt), do: trace([option])

  def trace(option) when is_atom(option), do: trace([option])

  def trace(options) when is_list(options), do: ...

  def trace(options), do: :erlang.error(:badarg, [options])

  def trace(:start, filename), do: trace([:start, {:file, filename}])

  def trace(:verbose, filename), do: trace([:start, :verbose, {:file, filename}])

  def trace(option, value) when is_atom(option), do: trace([{option, value}])

  def trace(option, value), do: :erlang.error(:badarg, [option, value])

  def trace_call_collapse([]), do: []

  def trace_call_collapse([_] = stack), do: stack

  def trace_call_collapse([_, _] = stack), do: stack

  def trace_call_collapse([_ | stack1] = stack), do: trace_call_collapse_1(stack, stack1, 1)

  def trace_off() do
    try do
      :erlang.trace_delivered(:all)
    catch
      {:error, :undef, _} ->
        :ok
    else
      ref ->
        receive do
        {:trace_delivered, :all, ^ref} ->
            :ok
        end
    end
    try do
      :erlang.trace(:all, false, [:all, :cpu_timestamp])
    catch
      {:error, :badarg, _} ->
        :erlang.trace(:all, false, [:all])
    end
    :erlang.trace_pattern(:on_load, false, [:local])
    :erlang.trace_pattern({:_, :_, :_}, false, [:local])
    :ok
  end

  def trace_on(procs, tracer, {v, cT}) do
    case cT do
      :cpu_time ->
        try do
          :erlang.trace(:all, true, [:cpu_timestamp])
        catch
          {:error, :badarg, _} ->
            {:error, :not_supported}
        else
          _ ->
            :ok
        end
      :wallclock ->
        :ok
    end
    |> case do
      :ok ->
        matchSpec = [{:_, [], [{:message, {{:cp, {:caller}}}}]}]
        :erlang.trace_pattern(:on_load, matchSpec, [:local])
        :erlang.trace_pattern({:_, :_, :_}, matchSpec, [:local])
        :lists.foreach(fn p ->
            :erlang.trace(p, true, [{:tracer, tracer} | trace_flags(v)])
        end, procs)
        :ok
      error ->
        error
    end
  end

  # Private Functions

  defp unquote(:"-apply_continue/4-fun-0-")(p0, p1, p2, p3) do
    # body not decompiled
  end

  defp unquote(:"-apply_start_stop/4-after$^1/0-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-apply_start_stop/4-fun-0-")(p0, p1, p2, p3) do
    # body not decompiled
  end

  defp unquote(:"-do_analyse_1/2-fun-0-")(p0, p1, p2, p3, p4) do
    # body not decompiled
  end

  defp unquote(:"-do_analyse_1/2-fun-1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-do_analyse_1/2-fun-2-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-do_analyse_1/2-fun-3-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-do_analyse_1/2-fun-4-")(p0, p1, p2, p3) do
    # body not decompiled
  end

  defp unquote(:"-end_of_trace/2-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-handle_req/3-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-handle_req/3-fun-1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-handle_req/3-fun-2-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-spawn_3step/4-fun-0-")(p0, p1, p2, p3) do
    # body not decompiled
  end

  defp unquote(:"-spawn_link_dbg_trace_client/4-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-spawn_link_trace_client/3-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-spawn_link_trace_client/3-fun-1-")(p0, p1, p2, p3, p4) do
    # body not decompiled
  end

  defp unquote(:"-spawn_link_trace_client/3-fun-2-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-spawn_link_trace_client/3-fun-3-")() do
    # body not decompiled
  end

  defp unquote(:"-start/0-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-start/0-fun-1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-start/0-fun-2-")() do
    # body not decompiled
  end

  defp unquote(:"-trace_on/3-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-try_pending_stop/1-fun-0-")(p0, p1) do
    # body not decompiled
  end

  def apply_1(function, args, options) do
    {[_, procs, continue], options_1} = getopts(options, [:start, :procs, :continue])
    procs_1 = case procs do
      [{:procs, p}] when is_list(p) ->
        p
      _ ->
        []
    end
    case continue do
      [] ->
        apply_start_stop(function, args, procs_1, options_1)
      [:continue] ->
        apply_continue(function, args, procs_1, options_1)
      _ ->
        :erlang.error(:badarg, [function, args, options])
    end
  end

  def apply_1(m, f, args, options) do
    arity = length(args)
    apply_1(&m.f/arity, args, options)
  end

  def apply_continue(function, args, procs, options) do
    ref = make_ref()
    parent = self()
    child = spawn(fn  ->
        mRef = :erlang.monitor(:process, parent)
        receive do
        {parent, ref, :start_trace} ->
            case trace([:start, {:procs, [parent | procs]} | options]) do
              :ok ->
                exit({ref, :trace_started})
              {:error, reason} ->
                exit(reason)
            end
          {:"DOWN", ^mRef, _, _, _} ->
            :done
        end
    end)
    mRef = :erlang.monitor(:process, child)
    try do
      send(child, {self(), ref, :start_trace})
    catch
      error -> error
    end
    receive do
    {:"DOWN", ^mRef, _, _, {^ref, :trace_started}} ->
        :erlang.apply(function, args)
      {:"DOWN", ^mRef, _, _, reason} ->
        exit(reason)
    end
  end

  def apply_start_stop(function, args, procs, options), do: ...

  def clock_add(table, id, clock, t) do
    dbg(1, 'clock_add(Table, ~w, ~w, ~w)~n', [id, clock, t])
    try do
      :ets.update_counter(table, id, {clock, t})
    :ok
    catch
      {:error, :badarg, _} ->
        :ets.insert(table, clocks(id: id))
        x = :ets.update_counter(table, id, {clock, t})
        cond do
          x >= 0 ->
            :ok
          true ->
            dbg(0, 'Negative counter value ~p ~p ~p ~p~n', [x, id, clock, t])
        end
        :ok
    end
  end

  def clocks_add(table, clocks(id: id) = clocks) do
    dbg(1, 'clocks_add(Table, ~w)~n', [clocks])
    case :ets.lookup(table, id) do
      [clocks0] ->
        :ets.insert(table, clocks_sum(clocks, clocks0, id))
      [] ->
        :ets.insert(table, clocks)
    end
  end

  def clocks_sort_r(l, e), do: clocks_sort_r_1(l, e, [])

  def clocks_sort_r_1([], _, r), do: postsort_r(:lists.sort(r))

  def clocks_sort_r_1([clocks() = c | l], e, r), do: clocks_sort_r_1(l, e, [[element(e, c) | c] | r])

  def clocks_sum(clocks(id: _Id1, cnt: cnt1, own: own1, acc: acc1), clocks(id: _Id2, cnt: cnt2, own: own2, acc: acc2), id), do: clocks(id: id, cnt: cnt1 + cnt2, own: own1 + own2, acc: acc1 + acc2)

  def dbg(level, f, a) when level >= 9 do
    :io.format(f, a)
    :ok
  end

  def dbg(_, _, _), do: :ok

  def do_analyse(table, analyse) do
    dbg(5, 'do_analyse_1(~p, ~p)~n', [table, analyse])
    result = try do
      do_analyse_1(table, analyse)
    catch
      {:throw, error, _} ->
        error
    end
    dbg(5, 'do_analyse_1(_, _) ->~p~n', [result])
    result
  end

  def do_analyse_1(table, analyse(group_leader: groupLeader, dest: io, cols: cols0, callers: printCallers, sort: sort, totals: printTotals, details: printDetails) = _Analyse), do: ...

  def dump(:undefined, _), do: false

  def dump(dump, term) do
    :io.format(dump, '~tp.~n', [parsify(term)])
    true
  end

  def dump_stack(:undefined, _, _), do: false

  def dump_stack(dump, stack, term) do
    {depth, _D} = case stack do
      :undefined ->
        {0, 0}
      _ ->
        case length(stack) do
          0 ->
            {0, 0}
          n ->
            {n, length(hd(stack))}
        end
    end
    :io.format(dump, '~s~tp.~n', [:lists.duplicate(depth, '  '), parsify(term)])
    true
  end

  def end_of_trace(table, tS) do
    procs = get()
    put(:table, table)
    dbg(2, 'get() -> ~p~n', [procs])
    _ = :lists.map(fn {pid, _} when is_pid(pid) ->
        trace_exit(table, pid, tS)
    end, procs)
    _ = erase()
    :ok
  end

  def ensure_open(pid, _Options) when is_pid(pid), do: {:already_open, pid}

  def ensure_open([], _Options), do: {:already_open, :undefined}

  def ensure_open(filename, options) when is_atom(filename) or is_list(filename), do: :file.open(filename, [{:encoding, :utf8} | options])

  def ets_select_foreach(table, matchSpec, limit, fun) do
    :ets.safe_fixtable(table, true)
    ets_select_foreach_1(:ets.select(table, matchSpec, limit), fun)
  end

  def ets_select_foreach_1(:"$end_of_table", _), do: :ok

  def ets_select_foreach_1({matches, continuation}, fun) do
    dbg(2, 'Matches = ~p~n', [matches])
    :lists.foreach(fun, matches)
    ets_select_foreach_1(:ets.select(continuation), fun)
  end

  def flat_format(f, trailer) when is_float(f), do: :lists.flatten([:io_lib.format('~.3f', [f]), trailer])

  def flat_format(w, trailer), do: :lists.flatten([:io_lib.format('~tp', [w]), trailer])

  def flat_format(term, trailer, width), do: flat_format(term, trailer, width, :left)

  def flat_format(term, trailer, width, :left), do: flat_format(term, trailer, width, {:left, ?\s})

  def flat_format(term, trailer, width, {:left, filler}), do: pad(flat_format(term, trailer), filler, width)

  def flat_format(term, trailer, width, :right), do: flat_format(term, trailer, width, {:right, ?\s})

  def flat_format(term, trailer, width, {:right, filler}), do: pad(filler, flat_format(term, trailer), width)

  def funcstat_pd(pid, func1, func0, clocks) do
    put({pid, func0}, (case get({pid, func0}) do
      :undefined ->
        funcstat(callers_sum: clocks(clocks, id: func0), called_sum: clocks(id: func0), callers: [clocks(clocks, id: func1)])
      funcstat(callers_sum: callersSum, callers: callers) = funcstatCallers ->
        funcstat(funcstatCallers, callers_sum: clocks_sum(callersSum, clocks, func0), callers: insert_call(clocks, func1, callers))
    end))
    put({pid, func1}, (case get({pid, func1}) do
      :undefined ->
        funcstat(callers_sum: clocks(id: func1), called_sum: clocks(clocks, id: func1), called: [clocks(clocks, id: func0)])
      funcstat(called_sum: calledSum, called: called) = funcstatCalled ->
        funcstat(funcstatCalled, called_sum: clocks_sum(calledSum, clocks, func1), called: insert_call(clocks, func0, called))
    end))
  end

  def funcstat_sort_r(funcstatList, element), do: funcstat_sort_r_1(funcstatList, element, [])

  def funcstat_sort_r_1([], _, r), do: postsort_r(:lists.sort(r))

  def funcstat_sort_r_1([funcstat(callers_sum: clocks() = clocks, callers: callers, called: called) = funcstat | l], element, r), do: funcstat_sort_r_1(l, element, [[element(element, clocks) | funcstat(funcstat, callers: clocks_sort_r(callers, element), called: clocks_sort_r(called, element))] | r])

  def get_stack(id) do
    case get(id) do
      :undefined ->
        []
      stack ->
        stack
    end
  end

  def getopts_1([], list, result), do: {:lists.reverse(result), list}

  def getopts_1([option | options], list, result) do
    {optvals, remaining} = getopts_2(list, option, [], [])
    getopts_1(options, remaining, [optvals | result])
  end

  def getopts_2([], _Option, result, remaining), do: {:lists.reverse(result), :lists.reverse(remaining)}

  def getopts_2([option | tail], ^option, result, remaining), do: getopts_2(tail, option, [option | result], remaining)

  def getopts_2([optval | tail], option, result, remaining) when element(1, optval) === option, do: getopts_2(tail, option, [optval | result], remaining)

  def getopts_2([other | tail], option, result, remaining), do: getopts_2(tail, option, result, [other | remaining])

  def handle_other({:"EXIT", pid, reason} = other, state) when is_pid(pid) or is_port(pid), do: ...

  def handle_other(other, state) do
    :io.format('~p:handle_other, unknown - ~p', [:fprof, other])
    state
  end

  def handle_req(trace_start(procs: procs, mode: mode, type: :file, dest: filename), tag, state) do
    case {get(:trace_state), get(:pending_stop)} do
      {:idle, []} ->
        trace_off()
        port = open_dbg_trace_port(:file, filename)
        case trace_on(procs, port, mode) do
          :ok ->
            put(:trace_state, :running)
            put(:trace_type, :file)
            put(:trace_pid, port)
            reply(tag, :ok)
            state
          error ->
            reply(tag, error)
            state
        end
      _ ->
        reply(tag, {:error, :already_tracing})
        state
    end
  end

  def handle_req(trace_start(procs: procs, mode: mode, type: :tracer, dest: tracer), tag, state) do
    case {get(:trace_state), get(:pending_stop)} do
      {:idle, []} ->
        trace_off()
        case trace_on(procs, tracer, mode) do
          :ok ->
            put(:trace_state, :running)
            put(:trace_type, :tracer)
            put(:trace_pid, tracer)
            reply(tag, :ok)
            state
          error ->
            reply(tag, error)
            state
        end
      _ ->
        reply(tag, {:error, :already_tracing})
        state
    end
  end

  def handle_req(trace_stop(), tag, state) do
    case get(:trace_state) do
      :running ->
        tracePid = get(:trace_pid)
        trace_off()
        case erase(:trace_type) do
          :file ->
            try do
              :erlang.port_close(tracePid)
            catch
              error -> error
            end
            put(:trace_state, :stopping)
            put(:trace_tag, tag)
            state
          :tracer ->
            erase(:trace_pid)
            put(:trace_state, :idle)
            case {get(:profile_state), get(:profile_type), get(:profile_pid)} do
              {:running, :tracer, tracePid} ->
                exit(tracePid, :normal)
                put(:profile_tag, tag)
                state
              _ ->
                reply(tag, :ok)
                try_pending_stop(state)
            end
        end
      _ ->
        reply(tag, {:error, :not_tracing})
        state
    end
  end

  def handle_req(profile(src: filename, group_leader: groupLeader, dump: dump, flags: flags), tag, state) do
    case {get(:profile_state), get(:pending_stop)} do
      {{:idle, _}, []} ->
        case ensure_open(dump, [:write | flags]) do
          {:already_open, dumpPid} ->
            put(:profile_dump, dumpPid)
            put(:profile_close_dump, false)
          {:ok, dumpPid} ->
            put(:profile_dump, dumpPid)
            put(:profile_close_dump, true)
          {:error, _} = error ->
            reply(tag, error)
            state
        end
        table = :ets.new(:fprof, [:set, :public, {:keypos, clocks(:id)}])
        pid = spawn_link_dbg_trace_client(filename, table, groupLeader, get(:profile_dump))
        put(:profile_state, :running)
        put(:profile_type, :file)
        put(:profile_pid, pid)
        put(:profile_tag, tag)
        put(:profile_table, table)
        state
      _ ->
        reply(tag, {:error, :already_profiling})
        state
    end
  end

  def handle_req(profile_start(group_leader: groupLeader, dump: dump, flags: flags), tag, state) do
    case {get(:profile_state), get(:pending_stop)} do
      {{:idle, _}, []} ->
        case ensure_open(dump, [:write | flags]) do
          {:already_open, dumpPid} ->
            put(:profile_dump, dumpPid)
            put(:profile_close_dump, false)
          {:ok, dumpPid} ->
            put(:profile_dump, dumpPid)
            put(:profile_close_dump, true)
          {:error, _} = error ->
            reply(tag, error)
            state
        end
        table = :ets.new(:fprof, [:set, :public, {:keypos, clocks(:id)}])
        pid = spawn_link_trace_client(table, groupLeader, get(:profile_dump))
        put(:profile_state, :running)
        put(:profile_type, :tracer)
        put(:profile_pid, pid)
        put(:profile_table, table)
        reply(tag, {:ok, pid})
        state
      _ ->
        reply(tag, {:error, :already_profiling})
        state
    end
  end

  def handle_req(profile_stop(), tag, state) do
    case {get(:profile_state), get(:profile_type)} do
      {:running, :tracer} ->
        profilePid = get(:profile_pid)
        case {get(:trace_state), get(:trace_type), get(:trace_pid)} do
          {:running, :tracer, profilePid} ->
            trace_off()
            erase(:trace_type)
            erase(:trace_pid)
            put(:trace_state, :idle)
          _ ->
            :ok
        end
        exit(profilePid, :normal)
        put(:profile_tag, tag)
        state
      {:running, :file} ->
        reply(tag, {:error, :profiling_file})
        state
      {_, _} ->
        reply(tag, {:error, :not_profiling})
        state
    end
  end

  def handle_req(analyse(dest: dest, flags: flags) = request, tag, state) do
    case get(:profile_state) do
      {:idle, :undefined} ->
        reply(tag, {:error, :no_profile})
        state
      {:idle, _} ->
        case ensure_open(dest, [:write | flags]) do
          {:error, _} = error ->
            reply(tag, error)
            state
          {destState, destPid} ->
            profileTable = get(:profile_table)
            reply(tag, spawn_3step(fn  ->
                do_analyse(profileTable, analyse(request, dest: destPid))
            end, fn result ->
                {result, :finish}
            end, fn :finish ->
                :ok
            end))
            case destState do
              :already_open ->
                :ok
              :ok ->
                :ok = :file.close(destPid)
            end
            state
        end
      _ ->
        reply(tag, {:error, :profiling})
        state
    end
  end

  def handle_req(stop(reason: reason), tag, state) do
    pendingStop = get(:pending_stop)
    case pendingStop do
      [] ->
        put(:stop_reason, reason)
      _ ->
        :ok
    end
    put(:pending_stop, [tag | pendingStop])
    try_pending_stop(state)
  end

  def handle_req(get_state(), tag, state) do
    reply(tag, {:ok, get()})
    state
  end

  def handle_req(save_profile(file: file), tag, state) do
    case get(:profile_state) do
      {:idle, :undefined} ->
        reply(tag, {:error, :no_profile})
      {:idle, _} ->
        reply(tag, :ets.tab2file(get(:profile_table), file))
        state
      _ ->
        reply(tag, {:error, :profiling})
        state
    end
  end

  def handle_req(load_profile(file: file), tag, state) do
    case get(:profile_state) do
      {:idle, result} ->
        case :ets.file2tab(file) do
          {:ok, table} ->
            put(:profile_state, {:idle, :ok})
            case result do
              {:error, :no_profile} ->
                :ets.delete(put(:profile_table, table))
              _ ->
                put(:profile_table, table)
            end
            reply(tag, :ok)
            state
          error ->
            reply(tag, error)
            state
        end
      _ ->
        reply(tag, {:error, :profiling})
        state
    end
  end

  def handle_req(request, tag, state) do
    :io.format('~n~p:handle_req, unknown request - ~p~n', [:fprof, request])
    reply(tag, {:error, :unknown_request})
    state
  end

  def handler(:end_of_trace, {:init, groupLeader, table, dump}) do
    dump(dump, :start_of_trace)
    dump(dump, :end_of_trace)
    info(groupLeader, dump, 'Empty trace!~n', [])
    end_of_trace(table, :undefined)
    :done
  end

  def handler(:end_of_trace, {:error, reason, _, groupLeader, dump}) do
    info(groupLeader, dump, '~nEnd of erroneous trace!~n', [])
    exit(reason)
  end

  def handler(:end_of_trace, {_, tS, groupLeader, table, dump}) do
    dump(dump, :end_of_trace)
    info(groupLeader, dump, '~nEnd of trace!~n', [])
    end_of_trace(table, tS)
    :done
  end

  def handler(trace, {:init, groupLeader, table, dump}) do
    dump(dump, :start_of_trace)
    info(groupLeader, dump, 'Reading trace data...~n', [])
    try do
      trace_handler(trace, table, groupLeader, dump)
    catch
      {:throw, error, _} ->
        dump(dump, {:error, error})
        end_of_trace(table, :undefined)
        {:error, error, 1, groupLeader, dump}
    else
      tS ->
        :ets.insert(table, misc(id: :first_ts, data: tS))
        :ets.insert(table, misc(id: :last_ts_n, data: {tS, 1}))
        {1, tS, groupLeader, table, dump}
    end
  end

  def handler(_, {:error, reason, m, groupLeader, dump}) do
    n = m + 1
    info_dots(groupLeader, dump, n)
    {:error, reason, n, groupLeader, dump}
  end

  def handler(trace, {m, tS0, groupLeader, table, dump}) do
    n = m + 1
    info_dots(groupLeader, dump, n)
    try do
      trace_handler(trace, table, groupLeader, dump)
    catch
      {:throw, error, _} ->
        dump(dump, {:error, error})
        end_of_trace(table, tS0)
        {:error, error, n, groupLeader, dump}
    else
      tS ->
        :ets.insert(table, misc(id: :last_ts_n, data: {tS, n}))
        {n, tS, groupLeader, table, dump}
    end
  end

  def info(groupLeader, ^groupLeader, _, _), do: :ok

  def info(groupLeader, _, format, list), do: :io.format(groupLeader, format, list)

  def info_dots(groupLeader, ^groupLeader, _), do: :ok

  def info_dots(groupLeader, _, n) do
    cond do
      rem(n, 100000) === 0 ->
        :io.format(groupLeader, ',~n', [])
      rem(n, 50000) === 0 ->
        :io.format(groupLeader, '.~n', [])
      rem(n, 1000) === 0 ->
        :io.put_chars(groupLeader, '.')
      true ->
        :ok
    end
  end

  def info_suspect_call(groupLeader, ^groupLeader, _, _), do: :ok

  def info_suspect_call(groupLeader, _, func, pid), do: :io.format(groupLeader, '~nWarning: ~tp called in ~p - trace may become corrupt!~n', parsify([func, pid]))

  def init_log(_Table, _Proc, :suspend), do: :ok

  def init_log(_Table, _Proc, :void), do: :ok

  def init_log(_Table, :undefined, _Entry), do: :ok

  def init_log(_Table, proc(init_cnt: 0), _Entry), do: :ok

  def init_log(table, proc(init_cnt: n, init_log: l) = proc, entry), do: :ets.insert(table, proc(proc, init_cnt: n - 1, init_log: [entry | l]))

  def init_log(table, id, entry) do
    proc = case :ets.lookup(table, id) do
      [p] ->
        p
      [] ->
        :undefined
    end
    init_log(table, proc, entry)
  end

  def insert_call(clocks, func, clocksList), do: insert_call(clocks, func, clocksList, [])

  def insert_call(clocks, func, [clocks(id: ^func) = c | t], acc), do: [clocks_sum(c, clocks, func) | t ++ acc]

  def insert_call(clocks, func, [h | t], acc), do: insert_call(clocks, func, t, [h | acc])

  def insert_call(clocks, func, [], acc), do: [clocks(clocks, id: func) | acc]

  def just_call(:undefined, _), do: {:"EXIT", :fprof_server, :noproc}

  def just_call(pid, request), do: ...

  def mfarity({m, f, args}) when is_list(args), do: {m, f, length(args)}

  def mfarity(mFA), do: mFA

  def open_dbg_trace_port(type, spec) do
    fun = :dbg.trace_port(type, spec)
    fun.()
  end

  def pad(char, l, size) when is_integer(char) and is_list(l) and is_integer(size) do
    list = :lists.flatten(l)
    length = length(list)
    cond do
      length >= size ->
        list
      true ->
        :lists.append(:lists.duplicate(size - length, char), list)
    end
  end

  def pad(l, char, size) when is_list(l) and is_integer(char) and is_integer(size) do
    list = :lists.flatten(l)
    length = length(list)
    cond do
      length >= size ->
        list
      true ->
        :lists.append(list, :lists.duplicate(size - length, char))
    end
  end

  def postsort_r(l), do: postsort_r(l, [])

  def postsort_r([], r), do: r

  def postsort_r([[_ | c] | l], r), do: postsort_r(l, [c | r])

  def print_called_1(dest, [clocks]), do: println(dest, '  {', clocks, '}]}.', "")

  def print_called_1(dest, [clocks | tail]) do
    println(dest, '  {', clocks, '},', "")
    print_called_1(dest, tail)
  end

  def print_callers_1(dest, [clocks]), do: println(dest, '  {', clocks, '}],', "")

  def print_callers_1(dest, [clocks | tail]) do
    println(dest, '  {', clocks, '},', "")
    print_callers_1(dest, tail)
  end

  def print_proc({:undefined, _}, _), do: :ok

  def print_proc(dest, proc(id: _Pid, parent: parent, spawned_as: spawnedAs, init_log: initLog)) do
    case {parent, spawnedAs, initLog} do
      {:undefined, :undefined, []} ->
        println(dest, '   ', [], '].', "")
      {_, :undefined, []} ->
        println(dest, ' { ', {:spawned_by, parsify(parent)}, '}].', "")
      _ ->
        println(dest, ' { ', {:spawned_by, parsify(parent)}, '},', "")
        case {spawnedAs, initLog} do
          {_, []} ->
            println(dest, ' { ', {:spawned_as, spawnedAs}, '}].', "")
          {:undefined, _} ->
            println(dest, ' { ', {:initial_calls, :lists.reverse(initLog)}, '}].', "")
          _ ->
            println(dest, ' { ', {:spawned_as, spawnedAs}, '},', "")
            println(dest, ' { ', {:initial_calls, :lists.reverse(initLog)}, '}].', "")
        end
    end
  end

  def println({:undefined, _}), do: :ok

  def println({io, _}), do: :io.nl(io)

  def result(:normal), do: :ok

  def result(reason), do: {:error, reason}

  def server_loop(state) do
    receive do
    {:fprof_server, {mref, pid} = tag, :"$code_change"} when is_reference(mref) and is_pid(pid) ->
        reply(tag, :ok)
        :fprof."$code_change"(state)
      {:fprof_server, {mref, pid} = tag, request} when is_reference(mref) and is_pid(pid) ->
        server_loop(handle_req(request, tag, state))
      other ->
        server_loop(handle_other(other, state))
    end
  end

  def spawn_3step(funPrelude, funAck, funBody), do: spawn_3step(:spawn, funPrelude, funAck, funBody)

  def spawn_3step(spawn, funPrelude, funAck, funBody) when spawn === :spawn or spawn === :spawn_link, do: ...

  def spawn_link_3step(funPrelude, funAck, funBody), do: spawn_3step(:spawn_link, funPrelude, funAck, funBody)

  def spawn_link_dbg_trace_client(file, table, groupLeader, dump) do
    case :dbg.trace_client(:file, file, {&handler/2, {:init, groupLeader, table, dump}}) do
      pid when is_pid(pid) ->
        link(pid)
        pid
      other ->
        exit(other)
    end
  end

  def spawn_link_trace_client(table, groupLeader, dump) do
    parent = self()
    spawn_link_3step(fn  ->
        process_flag(:trap_exit, true)
        {self(), :go}
    end, fn ack ->
        ack
    end, fn :go ->
        init = {:init, groupLeader, table, dump}
        tracer_loop(parent, &handler/2, init)
    end)
  end

  def trace_call(table, pid, func, tS, cP), do: ...

  def trace_call_collapse_1(stack, [], _), do: stack

  def trace_call_collapse_1([{func0, _} | _] = stack, [{^func0, _} | s1] = s, n) do
    case trace_call_collapse_2(stack, s, n) do
      true ->
        s
      false ->
        trace_call_collapse_1(stack, s1, n + 1)
    end
  end

  def trace_call_collapse_1(stack, [_ | s1], n), do: trace_call_collapse_1(stack, s1, n + 1)

  def trace_call_collapse_2(_, _, 0), do: true

  def trace_call_collapse_2([{func1, _} | [{func2, _} | _] = stack2], [{^func1, _} | [{^func2, _} | _] = s2], n), do: trace_call_collapse_2(stack2, s2, n - 1)

  def trace_call_collapse_2([{func1, _} | _], [{^func1, _} | _], _N), do: false

  def trace_call_collapse_2(_Stack, [_], _N), do: false

  def trace_call_collapse_2(stack, [_ | s], n), do: trace_call_collapse_2(stack, s, n)

  def trace_call_collapse_2(_Stack, [], _N), do: false

  def trace_call_push(table, pid, func, tS, stack) do
    case stack do
      [] ->
        :ok
      [_ | _] ->
        trace_clock(table, pid, tS, stack, clocks(:own))
    end
    newStack = [[{func, tS}] | stack]
    trace_clock(table, pid, 1, newStack, clocks(:cnt))
    newStack
  end

  def trace_call_shove(table, pid, func, tS, stack) do
    trace_clock(table, pid, tS, stack, clocks(:own))
    [[_ | newLevel0] | newStack1] = case stack do
      [] ->
        [[{func, tS}]]
      [level0 | stack1] ->
        [trace_call_collapse([{func, tS} | level0]) | stack1]
    end
    newStack = [[{func, tS} | newLevel0] | newStack1]
    trace_clock(table, pid, 1, newStack, clocks(:cnt))
    newStack
  end

  def trace_clock(_Table, _Pid, _T, [[{:suspend, _}], [{:suspend, _}] | _] = _Stack, _Clock) do
    dbg(9, 'trace_clock(Table, ~w, ~w, ~w, ~w)~n', [_Pid, _T, _Stack, _Clock])
    :ok
  end

  def trace_clock(table, pid, t, [[{:garbage_collect, tS0}], [{:suspend, _}]], clock), do: trace_clock_1(table, pid, t, tS0, :undefined, :garbage_collect, clock)

  def trace_clock(table, pid, t, [[{:garbage_collect, tS0}], [{:suspend, _}], [{func2, _} | _] | _], clock), do: trace_clock_1(table, pid, t, tS0, func2, :garbage_collect, clock)

  def trace_clock(table, pid, t, [[{func0, tS0}, {func1, _} | _] | _], clock), do: trace_clock_1(table, pid, t, tS0, func1, func0, clock)

  def trace_clock(table, pid, t, [[{func0, tS0}], [{func1, _} | _] | _], clock), do: trace_clock_1(table, pid, t, tS0, func1, func0, clock)

  def trace_clock(table, pid, t, [[{func0, tS0}]], clock), do: trace_clock_1(table, pid, t, tS0, :undefined, func0, clock)

  def trace_clock(_, _, _, [], _), do: :ok

  def trace_clock_1(table, pid, _, _, caller, :suspend, clocks(:own)), do: clock_add(table, {pid, caller, :suspend}, clocks(:own), 0)

  def trace_clock_1(table, pid, t, tS, caller, func, clock) do
    clock_add(table, {pid, caller, func}, clock, (cond do
      is_integer(t) ->
        t
      true ->
        ts_sub(t, tS)
    end))
  end

  def trace_exit(table, pid, tS) do
    stack = erase(pid)
    dbg(0, 'trace_exit(~p, ~p)~n~p~n', [pid, tS, stack])
    case stack do
      :undefined ->
        :ok
      [] ->
        :ok
      [_ | _] = stack ->
        _ = trace_return_to_int(table, pid, :undefined, tS, stack)
        :ok
    end
    :ok
  end

  def trace_flags(:normal), do: [:call, :return_to, :running, :procs, :garbage_collection, :arity, :timestamp, :set_on_spawn]

  def trace_flags(:verbose), do: [:call, :return_to, :send, :receive, :running, :procs, :garbage_collection, :timestamp, :set_on_spawn]

  def trace_gc_end(table, pid, tS) do
    stack = get(pid)
    dbg(0, 'trace_gc_end(~p, ~p)~n~p~n', [pid, tS, stack])
    case stack do
      :undefined ->
        put(pid, [])
      [] ->
        :ok
      [[{:garbage_collect, _}]] ->
        put(pid, trace_return_to_int(table, pid, :undefined, tS, stack))
      [[{:garbage_collect, _}], [{func1, _} | _] | _] ->
        put(pid, trace_return_to_int(table, pid, func1, tS, stack))
      _ ->
        throw({:inconsistent_trace_data, :fprof, 2139, [pid, tS, stack]})
    end
  end

  def trace_gc_start(table, pid, tS) do
    stack = get_stack(pid)
    dbg(0, 'trace_gc_start(~p, ~p)~n~p~n', [pid, tS, stack])
    put(pid, trace_call_push(table, pid, :garbage_collect, tS, stack))
  end

  def trace_handler({:trace_ts, pid, :call, _MFA, _TS} = trace, _Table, _, dump) do
    stack = get(pid)
    dump_stack(dump, stack, trace)
    throw({:incorrect_trace_data, :fprof, 1522, [trace, stack]})
  end

  def trace_handler({:trace_ts, pid, :call, {_M, _F, arity} = func, {:cp, cP}, tS} = trace, table, groupLeader, dump) when is_integer(arity) do
    dump_stack(dump, get(pid), trace)
    case func do
      {:erlang, :trace, 3} ->
        info_suspect_call(groupLeader, dump, func, pid)
      {:erlang, :trace_pattern, 3} ->
        info_suspect_call(groupLeader, dump, func, pid)
      _ ->
        :ok
    end
    trace_call(table, pid, func, tS, cP)
    tS
  end

  def trace_handler({:trace_ts, pid, :call, {_M, _F, args} = mFArgs, {:cp, cP}, tS} = trace, table, _, dump) when is_list(args) do
    dump_stack(dump, get(pid), trace)
    func = mfarity(mFArgs)
    trace_call(table, pid, func, tS, cP)
    tS
  end

  def trace_handler({:trace_ts, pid, :return_to, :undefined, tS} = trace, table, _, dump) do
    dump_stack(dump, get(pid), trace)
    trace_return_to(table, pid, :undefined, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :return_to, {_M, _F, arity} = func, tS} = trace, table, _, dump) when is_integer(arity) do
    dump_stack(dump, get(pid), trace)
    trace_return_to(table, pid, func, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :return_to, {_M, _F, args} = mFArgs, tS} = trace, table, _, dump) when is_list(args) do
    dump_stack(dump, get(pid), trace)
    func = mfarity(mFArgs)
    trace_return_to(table, pid, func, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :spawn, child, mFArgs, tS} = trace, table, _, dump) do
    dump_stack(dump, get(pid), trace)
    trace_spawn(table, child, mFArgs, tS, pid)
    tS
  end

  def trace_handler({:trace_ts, pid, :spawned, parent, mFArgs, tS} = trace, table, _, dump) do
    dump_stack(dump, get(pid), trace)
    trace_spawn(table, pid, mFArgs, tS, parent)
    tS
  end

  def trace_handler({:trace_ts, pid, :exit, _Reason, tS} = trace, table, _, dump) do
    dump_stack(dump, get(pid), trace)
    trace_exit(table, pid, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :out, 0, tS} = trace, table, _, dump) do
    dump_stack(dump, get(pid), trace)
    trace_out(table, pid, :undefined, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :out, {_M, _F, arity} = func, tS} = trace, table, _, dump) when is_integer(arity) do
    dump_stack(dump, get(pid), trace)
    trace_out(table, pid, func, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :out, {_M, _F, args} = mFArgs, tS} = trace, table, _, dump) when is_list(args) do
    dump_stack(dump, get(pid), trace)
    func = mfarity(mFArgs)
    trace_out(table, pid, func, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :in, 0, tS} = trace, table, _, dump) do
    dump_stack(dump, get(pid), trace)
    trace_in(table, pid, :undefined, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :in, {_M, _F, arity} = func, tS} = trace, table, _, dump) when is_integer(arity) do
    dump_stack(dump, get(pid), trace)
    trace_in(table, pid, func, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :in, {_M, _F, args} = mFArgs, tS} = trace, table, _, dump) when is_list(args) do
    dump_stack(dump, get(pid), trace)
    func = mfarity(mFArgs)
    trace_in(table, pid, func, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :gc_minor_start, _Func, tS} = trace, table, _, dump) do
    dump_stack(dump, get(pid), trace)
    trace_gc_start(table, pid, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :gc_major_start, _Func, tS} = trace, table, _, dump) do
    dump_stack(dump, get(pid), trace)
    trace_gc_start(table, pid, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :gc_start, _Func, tS} = trace, table, _, dump) do
    dump_stack(dump, get(pid), trace)
    trace_gc_start(table, pid, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :gc_minor_end, _Func, tS} = trace, table, _, dump) do
    dump_stack(dump, get(pid), trace)
    trace_gc_end(table, pid, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :gc_major_end, _Func, tS} = trace, table, _, dump) do
    dump_stack(dump, get(pid), trace)
    trace_gc_end(table, pid, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :gc_end, _Func, tS} = trace, table, _, dump) do
    dump_stack(dump, get(pid), trace)
    trace_gc_end(table, pid, tS)
    tS
  end

  def trace_handler({:trace_ts, pid, :link, _OtherPid, tS} = trace, _Table, _, dump) do
    dump_stack(dump, get(pid), trace)
    tS
  end

  def trace_handler({:trace_ts, pid, :unlink, _OtherPid, tS} = trace, _Table, _, dump) do
    dump_stack(dump, get(pid), trace)
    tS
  end

  def trace_handler({:trace_ts, pid, :getting_linked, _OtherPid, tS} = trace, _Table, _, dump) do
    dump_stack(dump, get(pid), trace)
    tS
  end

  def trace_handler({:trace_ts, pid, :getting_unlinked, _OtherPid, tS} = trace, _Table, _, dump) do
    dump_stack(dump, get(pid), trace)
    tS
  end

  def trace_handler({:trace_ts, pid, :register, _Name, tS} = trace, _Table, _, dump) do
    dump_stack(dump, get(pid), trace)
    tS
  end

  def trace_handler({:trace_ts, pid, :unregister, _Name, tS} = trace, _Table, _, dump) do
    dump_stack(dump, get(pid), trace)
    tS
  end

  def trace_handler({:trace_ts, pid, :send, _OtherPid, _Msg, tS} = trace, _Table, _, dump) do
    dump_stack(dump, get(pid), trace)
    tS
  end

  def trace_handler({:trace_ts, pid, :send_to_non_existing_process, _OtherPid, _Msg, tS} = trace, _Table, _, dump) do
    dump_stack(dump, get(pid), trace)
    tS
  end

  def trace_handler({:trace_ts, pid, :receive, _Msg, tS} = trace, _Table, _, dump) do
    dump_stack(dump, get(pid), trace)
    tS
  end

  def trace_handler(trace, _Table, _, dump) do
    dump(dump, trace)
    throw({:incorrect_trace_data, :fprof, 1720, [trace]})
  end

  def trace_in(table, pid, func, tS) do
    stack = get(pid)
    dbg(0, 'trace_in(~p, ~p, ~p)~n~p~n', [pid, func, tS, stack])
    case stack do
      :undefined ->
        put(pid, [[{func, tS}]])
      [] ->
        put(pid, [[{func, tS}]])
      [[{:suspend, _}]] ->
        put(pid, trace_return_to_int(table, pid, :undefined, tS, stack))
      [[{:suspend, _}] | [[{:suspend, _}] | _] = newStack] ->
        put(pid, newStack)
      [[{:suspend, _}], [{func1, _} | _] | _] ->
        put(pid, trace_return_to_int(table, pid, func1, tS, stack))
      _ ->
        throw({:inconsistent_trace_data, :fprof, 2113, [pid, func, tS, stack]})
    end
  end

  def trace_out(table, pid, func, tS) do
    stack = get_stack(pid)
    dbg(0, 'trace_out(~p, ~p, ~p)~n~p~n', [pid, func, tS, stack])
    case stack do
      [] ->
        put(pid, trace_call_push(table, pid, :suspend, tS, (case func do
          :undefined ->
            []
          _ ->
            [[{func, tS}]]
        end)))
      [[{:suspend, _}] | _] ->
        put(pid, [[{:suspend, tS}] | stack])
      [_ | _] ->
        put(pid, trace_call_push(table, pid, :suspend, tS, stack))
    end
  end

  def trace_return_to(table, pid, func, tS) do
    stack = get_stack(pid)
    dbg(0, 'trace_return_to(~p, ~p, ~p)~n~p~n', [pid, func, tS, stack])
    case stack do
      [[{:suspend, _} | _] | _] ->
        throw({:inconsistent_trace_data, :fprof, 1950, [pid, func, tS, stack]})
      [[{:garbage_collect, _} | _] | _] ->
        throw({:inconsistent_trace_data, :fprof, 1953, [pid, func, tS, stack]})
      [_ | _] ->
        put(pid, trace_return_to_int(table, pid, func, tS, stack))
      [] ->
        put(pid, trace_return_to_int(table, pid, func, tS, stack))
    end
    :ok
  end

  def trace_return_to_1(_, _, :undefined, _, []), do: {[], []}

  def trace_return_to_1(_, _, _, _, []), do: {:undefined, []}

  def trace_return_to_1(table, pid, func, tS, [[{^func, _} | level0] | stack1] = stack) do
    charged = trace_return_to_3([level0 | stack1], [])
    case :lists.member(func, charged) do
      false ->
        trace_clock(table, pid, tS, stack, clocks(:acc))
        {stack, [func | charged]}
      true ->
        {stack, charged}
    end
  end

  def trace_return_to_1(table, pid, func, tS, stack), do: trace_return_to_2(table, pid, func, tS, stack)

  def trace_return_to_2(table, pid, func, tS, [] = stack), do: trace_return_to_1(table, pid, func, tS, stack)

  def trace_return_to_2(table, pid, func, tS, [[] | stack1]), do: trace_return_to_1(table, pid, func, tS, stack1)

  def trace_return_to_2(table, pid, func, tS, [[{func0, _} | level1] | stack1] = stack) do
    case trace_return_to_2(table, pid, func, tS, [level1 | stack1]) do
      {:undefined, _} = r ->
        r
      {newStack, charged} = r ->
        case :lists.member(func0, charged) do
          false ->
            trace_clock(table, pid, tS, stack, clocks(:acc))
            {newStack, [func0 | charged]}
          true ->
            r
        end
    end
  end

  def trace_return_to_3([], r), do: r

  def trace_return_to_3([[] | stack1], r), do: trace_return_to_3(stack1, r)

  def trace_return_to_3([[{func0, _} | level0] | stack1], r), do: trace_return_to_3([level0 | stack1], [func0 | r])

  def trace_return_to_int(table, pid, func, tS, stack) do
    trace_clock(table, pid, tS, stack, clocks(:own))
    case trace_return_to_2(table, pid, func, tS, stack) do
      {:undefined, _} ->
        [[{func, tS}] | stack]
      {[[{func, _} | level0] | stack1], _} ->
        [[{func, tS} | level0] | stack1]
      {newStack, _} ->
        newStack
    end
  end

  def trace_spawn(table, pid, mFArgs, tS, parent) do
    stack = get(pid)
    dbg(0, 'trace_spawn(~p, ~p, ~p, ~p)~n~p~n', [pid, mFArgs, tS, parent, stack])
    case stack do
      :undefined ->
        {m, f, args} = mFArgs
        oldStack = [[{{m, f, length(args)}, tS}]]
        put(pid, trace_call_push(table, pid, :suspend, tS, oldStack))
        :ets.insert(table, proc(id: pid, parent: parent, spawned_as: mFArgs))
      _ ->
        :ok
    end
  end

  def tracer_loop(parent, handler, state) do
    receive do
    trace when element(1, trace) === :trace ->
        tracer_loop(parent, handler, handler.(trace, state))
      trace when element(1, trace) === :trace_ts ->
        tracer_loop(parent, handler, handler.(trace, state))
      {:"EXIT", ^parent, reason} ->
        _ = handler(:end_of_trace, state)
        exit(reason)
      _ ->
        tracer_loop(parent, handler, state)
    end
  end

  def try_pending_stop(state) do
    case {get(:trace_state), get(:profile_state), get(:pending_stop)} do
      {:idle, {:idle, _}, [_ | _] = pendingStop} ->
        reason = get(:stop_reason)
        reply = result(reason)
        :lists.foreach(fn tag ->
            reply(tag, reply)
        end, pendingStop)
        exit(reason)
      _ ->
        state
    end
  end

  def ts_sub({a, b, c} = _T, {a0, b0, c0} = _T0) do
    x = a - a0 * 1000000 + b - b0 * 1000000 + c - c0
    cond do
      x >= 0 ->
        :ok
      true ->
        dbg(9, 'Negative counter value ~p ~p ~p~n', [x, _T, _T0])
    end
    x
  end

  def ts_sub(_, _), do: :undefined
end
