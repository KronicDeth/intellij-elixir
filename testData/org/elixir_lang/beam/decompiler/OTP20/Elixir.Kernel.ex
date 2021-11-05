# Source code recreated from a .beam file by IntelliJ Elixir
defmodule Kernel do

  # Macros

  defmacro unquote(:!)(p0) do
    # body not decompiled
  end

  defmacro left && right do
    # body not decompiled
  end

  defmacro left .. right do
    # body not decompiled
  end

  defmacro left <> right do
    # body not decompiled
  end

  defmacro unquote(:@)(p0) do
    # body not decompiled
  end

  defmacro alias!(p0) do
    # body not decompiled
  end

  defmacro left and right do
    # body not decompiled
  end

  defmacro binding() do
    # body not decompiled
  end

  defmacro binding(p0) do
    # body not decompiled
  end

  defmacro def(p0) do
    # body not decompiled
  end

  defmacro def(p0, p1) do
    # body not decompiled
  end

  defmacro defdelegate(p0, p1) do
    # body not decompiled
  end

  defmacro defexception(p0) do
    # body not decompiled
  end

  defmacro defimpl(p0, p1) do
    # body not decompiled
  end

  defmacro defimpl(p0, p1, p2) do
    # body not decompiled
  end

  defmacro defmacro(p0) do
    # body not decompiled
  end

  defmacro defmacro(p0, p1) do
    # body not decompiled
  end

  defmacro defmacrop(p0) do
    # body not decompiled
  end

  defmacro defmacrop(p0, p1) do
    # body not decompiled
  end

  defmacro defmodule(p0, p1) do
    # body not decompiled
  end

  defmacro defoverridable(p0) do
    # body not decompiled
  end

  defmacro defp(p0) do
    # body not decompiled
  end

  defmacro defp(p0, p1) do
    # body not decompiled
  end

  defmacro defprotocol(p0, p1) do
    # body not decompiled
  end

  defmacro defstruct(p0) do
    # body not decompiled
  end

  defmacro destructure(p0, p1) do
    # body not decompiled
  end

  defmacro get_and_update_in(p0, p1) do
    # body not decompiled
  end

  defmacro if(p0, p1) do
    # body not decompiled
  end

  defmacro left in right do
    # body not decompiled
  end

  defmacro is_nil(p0) do
    # body not decompiled
  end

  defmacro match?(p0, p1) do
    # body not decompiled
  end

  defmacro left or right do
    # body not decompiled
  end

  defmacro pop_in(p0) do
    # body not decompiled
  end

  defmacro put_in(p0, p1) do
    # body not decompiled
  end

  defmacro raise(p0) do
    # body not decompiled
  end

  defmacro raise(p0, p1) do
    # body not decompiled
  end

  defmacro reraise(p0, p1) do
    # body not decompiled
  end

  defmacro reraise(p0, p1, p2) do
    # body not decompiled
  end

  defmacro sigil_C(p0, p1) do
    # body not decompiled
  end

  defmacro sigil_D(p0, p1) do
    # body not decompiled
  end

  defmacro sigil_N(p0, p1) do
    # body not decompiled
  end

  defmacro sigil_R(p0, p1) do
    # body not decompiled
  end

  defmacro sigil_S(p0, p1) do
    # body not decompiled
  end

  defmacro sigil_T(p0, p1) do
    # body not decompiled
  end

  defmacro sigil_W(p0, p1) do
    # body not decompiled
  end

  defmacro sigil_c(p0, p1) do
    # body not decompiled
  end

  defmacro sigil_r(p0, p1) do
    # body not decompiled
  end

  defmacro sigil_s(p0, p1) do
    # body not decompiled
  end

  defmacro sigil_w(p0, p1) do
    # body not decompiled
  end

  defmacro to_char_list(p0) do
    # body not decompiled
  end

  defmacro to_charlist(p0) do
    # body not decompiled
  end

  defmacro to_string(p0) do
    # body not decompiled
  end

  defmacro unless(p0, p1) do
    # body not decompiled
  end

  defmacro update_in(p0, p1) do
    # body not decompiled
  end

  defmacro use(p0) do
    # body not decompiled
  end

  defmacro use(p0, p1) do
    # body not decompiled
  end

  defmacro var!(p0) do
    # body not decompiled
  end

  defmacro var!(p0, p1) do
    # body not decompiled
  end

  defmacro left |> right do
    # body not decompiled
  end

  defmacro left || right do
    # body not decompiled
  end

  # Functions

  @spec !=(term(), term()) :: boolean()
  def left@1 != right@1 do
    left@1 != right@1
  end

  @spec !==(term(), term()) :: boolean()
  def left@1 !== right@1 do
    left@1 !== right@1
  end

  @spec *(unknown_type, integer()) :: unknown_type
  @spec *(integer(), unknown_type) :: unknown_type
  @spec *(unknown_type, unknown_type) :: unknown_type
  @spec *(integer(), integer()) :: integer()
  def left@1 * right@1 do
    left@1 * right@1
  end

  @spec +(value) :: value when value: number()
  def (+value@1) do
    +value@1
  end

  @spec +(unknown_type, integer()) :: unknown_type
  @spec +(integer(), unknown_type) :: unknown_type
  @spec +(unknown_type, unknown_type) :: unknown_type
  @spec +(integer(), integer()) :: integer()
  def left@1 + right@1 do
    left@1 + right@1
  end

  @spec ++([], term()) :: maybe_improper_list()
  def left@1 ++ right@1 do
    left@1 ++ right@1
  end

  @spec -(unknown_type) :: unknown_type
  @spec -(neg_integer()) :: pos_integer()
  @spec -(pos_integer()) :: neg_integer()
  @spec -(0) :: 0
  def (-value@1) do
    -value@1
  end

  @spec -(unknown_type, integer()) :: unknown_type
  @spec -(integer(), unknown_type) :: unknown_type
  @spec -(unknown_type, unknown_type) :: unknown_type
  @spec -(integer(), integer()) :: integer()
  def left@1 - right@1 do
    left@1 - right@1
  end

  @spec --([], []) :: []
  def left@1 -- right@1 do
    left@1 -- right@1
  end

  @spec /(number(), number()) :: unknown_type
  def left@1 / right@1 do
    left@1 / right@1
  end

  @spec <(term(), term()) :: boolean()
  def left@1 < right@1 do
    left@1 < right@1
  end

  @spec <=(term(), term()) :: boolean()
  def left@1 <= right@1 do
    left@1 <= right@1
  end

  @spec ==(term(), term()) :: boolean()
  def left@1 == right@1 do
    left@1 == right@1
  end

  @spec ===(term(), term()) :: boolean()
  def left@1 === right@1 do
    left@1 === right@1
  end

  @spec =~(String.t(), (String.t() | Regex.t())) :: boolean()
  def left@1 =~ <<>> when :erlang.is_binary(left@1) do
    true
  end

  def left@1 =~ right@1 when :erlang.is_binary(left@1) and :erlang.is_binary(right@1) do
    :binary.match(left@1, right@1) != :nomatch
  end

  def left@1 =~ right@1 when :erlang.is_binary(left@1) do
    Regex.match?(right@1, left@1)
  end

  @spec >(term(), term()) :: boolean()
  def left@1 > right@1 do
    left@1 > right@1
  end

  @spec >=(term(), term()) :: boolean()
  def left@1 >= right@1 do
    left@1 >= right@1
  end

  @spec __info__((:attributes | :compile | :exports | :functions | :macros | :md5 | :module | :native_addresses)) :: (atom() | [({atom(), any()} | {atom(), byte(), integer()})])
  def __info__(:functions) do
    [{:!=, 2}, {:!==, 2}, {:*, 2}, {:+, 1}, {:+, 2}, {:++, 2}, {:-, 1}, {:-, 2}, {:--, 2}, {:/, 2}, {:<, 2}, {:<=, 2}, {:==, 2}, {:===, 2}, {:=~, 2}, {:>, 2}, {:>=, 2}, {:abs, 1}, {:apply, 2}, {:apply, 3}, {:binary_part, 3}, {:bit_size, 1}, {:byte_size, 1}, {:div, 2}, {:elem, 2}, {:exit, 1}, {:function_exported?, 3}, {:get_and_update_in, 3}, {:get_in, 2}, {:hd, 1}, {:inspect, 1}, {:inspect, 2}, {:is_atom, 1}, {:is_binary, 1}, {:is_bitstring, 1}, {:is_boolean, 1}, {:is_float, 1}, {:is_function, 1}, {:is_function, 2}, {:is_integer, 1}, {:is_list, 1}, {:is_map, 1}, {:is_number, 1}, {:is_pid, 1}, {:is_port, 1}, {:is_reference, 1}, {:is_tuple, 1}, {:length, 1}, {:macro_exported?, 3}, {:make_ref, 0}, {:map_size, 1}, {:max, 2}, {:min, 2}, {:node, 0}, {:node, 1}, {:not, 1}, {:pop_in, 2}, {:put_elem, 3}, {:put_in, 3}, {:rem, 2}, {:round, 1}, {:self, 0}, {:send, 2}, {:spawn, 1}, {:spawn, 3}, {:spawn_link, 1}, {:spawn_link, 3}, {:spawn_monitor, 1}, {:spawn_monitor, 3}, {:struct, 1}, {:struct, 2}, {:struct!, 1}, {:struct!, 2}, {:throw, 1}, {:tl, 1}, {:trunc, 1}, {:tuple_size, 1}, {:update_in, 3}]
  end

  def __info__(:macros) do
    [{:!, 1}, {:&&, 2}, {:"..", 2}, {:<>, 2}, {:@, 1}, {:alias!, 1}, {:and, 2}, {:binding, 0}, {:binding, 1}, {:def, 1}, {:def, 2}, {:defdelegate, 2}, {:defexception, 1}, {:defimpl, 2}, {:defimpl, 3}, {:defmacro, 1}, {:defmacro, 2}, {:defmacrop, 1}, {:defmacrop, 2}, {:defmodule, 2}, {:defoverridable, 1}, {:defp, 1}, {:defp, 2}, {:defprotocol, 2}, {:defstruct, 1}, {:destructure, 2}, {:get_and_update_in, 2}, {:if, 2}, {:in, 2}, {:is_nil, 1}, {:match?, 2}, {:or, 2}, {:pop_in, 1}, {:put_in, 2}, {:raise, 1}, {:raise, 2}, {:reraise, 2}, {:reraise, 3}, {:sigil_C, 2}, {:sigil_D, 2}, {:sigil_N, 2}, {:sigil_R, 2}, {:sigil_S, 2}, {:sigil_T, 2}, {:sigil_W, 2}, {:sigil_c, 2}, {:sigil_r, 2}, {:sigil_s, 2}, {:sigil_w, 2}, {:to_char_list, 1}, {:to_charlist, 1}, {:to_string, 1}, {:unless, 2}, {:update_in, 2}, {:use, 1}, {:use, 2}, {:var!, 1}, {:var!, 2}, {:|>, 2}, {:||, 2}]
  end

  def __info__(info) do
    :erlang.get_module_info(Kernel, info)
  end

  @spec abs(number()) :: number()
  def abs(number@1) do
    :erlang.abs(number@1)
  end

  @spec apply(function(), [any()]) :: any()
  def apply(fun@1, args@1) do
    :erlang.apply(fun@1, args@1)
  end

  @spec apply(module(), atom(), [any()]) :: any()
  def apply(module@1, fun@1, args@1) do
    :erlang.apply(module@1, fun@1, args@1)
  end

  @spec binary_part(binary(), pos_integer(), integer()) :: binary()
  def binary_part(binary@1, start@1, length@1) do
    :erlang.binary_part(binary@1, start@1, length@1)
  end

  @spec bit_size(unknown_type) :: non_neg_integer()
  def bit_size(bitstring@1) do
    :erlang.bit_size(bitstring@1)
  end

  @spec byte_size(unknown_type) :: non_neg_integer()
  def byte_size(bitstring@1) do
    :erlang.byte_size(bitstring@1)
  end

  @spec div(integer(), (neg_integer() | pos_integer())) :: integer()
  def div(dividend@1, divisor@1) do
    div(dividend@1, divisor@1)
  end

  @spec elem(tuple(), non_neg_integer()) :: term()
  def elem(tuple@1, index@1) do
    :erlang.element(index@1 + 1, tuple@1)
  end

  @spec exit(term()) :: no_return()
  def exit(reason@1) do
    :erlang.exit(reason@1)
  end

  @spec function_exported?(module(), atom(), arity()) :: boolean()
  def function_exported?(module@1, function@1, arity@1) do
    :erlang.function_exported(module@1, function@1, arity@1)
  end

  @spec get_and_update_in(structure :: Access.t(), keys, (term() -> ({get_value, update_value} | :pop))) :: {get_value, structure :: Access.t()} when keys: [any(), ...], update_value: term()
  def get_and_update_in(data@1, [head@1], fun@1) when :erlang.is_function(head@1, 3) do
    head@1.(:get_and_update, data@1, fun@1)
  end

  def get_and_update_in(data@1, [head@1 | tail@1], fun@1) when :erlang.is_function(head@1, 3) do
    head@1.(:get_and_update, data@1, fn _@1 ->
        Kernel.get_and_update_in(_@1, tail@1, fun@1)
    end)
  end

  def get_and_update_in(data@1, [head@1], fun@1) when :erlang.is_function(fun@1, 1) do
    Access.get_and_update(data@1, head@1, fun@1)
  end

  def get_and_update_in(data@1, [head@1 | tail@1], fun@1) when :erlang.is_function(fun@1, 1) do
    Access.get_and_update(data@1, head@1, fn _@1 ->
        Kernel.get_and_update_in(_@1, tail@1, fun@1)
    end)
  end

  @spec get_in(Access.t(), [term(), ...]) :: term()
  def get_in(data@1, [h@1]) when :erlang.is_function(h@1) do
    h@1.(:get, data@1, fn _@1 ->
        _@1
    end)
  end

  def get_in(data@1, [h@1 | t@1]) when :erlang.is_function(h@1) do
    h@1.(:get, data@1, fn _@1 ->
        Kernel.get_in(_@1, t@1)
    end)
  end

  def get_in(nil, [_]) do
    nil
  end

  def get_in(nil, [_ | t@1]) do
    Kernel.get_in(nil, t@1)
  end

  def get_in(data@1, [h@1]) do
    Access.get(data@1, h@1)
  end

  def get_in(data@1, [h@1 | t@1]) do
    Kernel.get_in(Access.get(data@1, h@1), t@1)
  end

  @spec hd(unknown_type) :: elem when elem: term()
  def hd(list@1) do
    :erlang.hd(list@1)
  end

  def inspect(x0@1) do
    inspect(x0@1, [])
  end

  @spec inspect(Inspect.t(), Keyword.t()) :: String.t()
  def inspect(arg@1, opts@1) when :erlang.is_list(opts@1) do
    opts@2 = Kernel.struct(Inspect.Opts, opts@1)
    limit@1 = case opts@2 do
      %{:pretty => _@1} ->
        _@1
      _@1 when :erlang.is_map(_@1) ->
        :erlang.error({:badkey, :pretty, _@1})
      _@1 ->
        _@1.pretty()
    end
    |> case do
      true ->
        case opts@2 do
          %{:width => _@2} ->
            _@2
          _@2 when :erlang.is_map(_@2) ->
            :erlang.error({:badkey, :width, _@2})
          _@2 ->
            _@2.width()
        end
      false ->
        :infinity
    end
    :erlang.iolist_to_binary(Inspect.Algebra.format(Inspect.Algebra.to_doc(arg@1, opts@2), limit@1))
  end

  @spec is_atom(term()) :: boolean()
  def is_atom(term@1) do
    :erlang.is_atom(term@1)
  end

  @spec is_binary(term()) :: boolean()
  def is_binary(term@1) do
    :erlang.is_binary(term@1)
  end

  @spec is_bitstring(term()) :: boolean()
  def is_bitstring(term@1) do
    :erlang.is_bitstring(term@1)
  end

  @spec is_boolean(term()) :: boolean()
  def is_boolean(term@1) do
    :erlang.is_boolean(term@1)
  end

  @spec is_float(term()) :: boolean()
  def is_float(term@1) do
    :erlang.is_float(term@1)
  end

  @spec is_function(term()) :: boolean()
  def is_function(term@1) do
    :erlang.is_function(term@1)
  end

  @spec is_function(term(), non_neg_integer()) :: boolean()
  def is_function(term@1, arity@1) do
    :erlang.is_function(term@1, arity@1)
  end

  @spec is_integer(term()) :: boolean()
  def is_integer(term@1) do
    :erlang.is_integer(term@1)
  end

  @spec is_list(term()) :: boolean()
  def is_list(term@1) do
    :erlang.is_list(term@1)
  end

  @spec is_map(term()) :: boolean()
  def is_map(term@1) do
    :erlang.is_map(term@1)
  end

  @spec is_number(term()) :: boolean()
  def is_number(term@1) do
    :erlang.is_number(term@1)
  end

  @spec is_pid(term()) :: boolean()
  def is_pid(term@1) do
    :erlang.is_pid(term@1)
  end

  @spec is_port(term()) :: boolean()
  def is_port(term@1) do
    :erlang.is_port(term@1)
  end

  @spec is_reference(term()) :: boolean()
  def is_reference(term@1) do
    :erlang.is_reference(term@1)
  end

  @spec is_tuple(term()) :: boolean()
  def is_tuple(term@1) do
    :erlang.is_tuple(term@1)
  end

  @spec length([]) :: non_neg_integer()
  def length(list@1) do
    :erlang.length(list@1)
  end

  @spec macro_exported?(module(), atom(), arity()) :: boolean()
  def macro_exported?(module@1, macro@1, arity@1) when :erlang.is_atom(module@1) and :erlang.is_atom(macro@1) and :erlang.is_integer(arity@1) and arity@1 >= 0 and arity@1 <= 255 do
    case Kernel.function_exported?(module@1, :__info__, 1) do
      true ->
        :lists.member({macro@1, arity@1}, module@1.__info__(:macros))
      false ->
        false
      _@1 ->
        :erlang.error({:badbool, :and, _@1})
    end
  end

  @spec make_ref() :: reference()
  def make_ref() do
    :erlang.make_ref()
  end

  @spec map_size(map()) :: non_neg_integer()
  def map_size(map@1) do
    :erlang.map_size(map@1)
  end

  @spec max(first, second) :: (first | second) when first: term(), second: term()
  def max(first@1, second@1) do
    :erlang.max(first@1, second@1)
  end

  @spec min(first, second) :: (first | second) when first: term(), second: term()
  def min(first@1, second@1) do
    :erlang.min(first@1, second@1)
  end

  def module_info() do
    # body not decompiled
  end

  def module_info(p0) do
    # body not decompiled
  end

  @spec node() :: node()
  def node() do
    :erlang.node()
  end

  @spec node((pid() | reference() | port())) :: node()
  def node(arg@1) do
    :erlang.node(arg@1)
  end

  @spec not(false) :: true
  @spec not(true) :: false
  def not(arg@1) do
    notarg@1
  end

  @spec pop_in(Access.t(), [term(), ...]) :: {term(), Access.t()}
  def pop_in(nil, [h@1 | _]) do
    Access.pop(nil, h@1)
  end

  def pop_in(data@1, keys@1) do
    do_pop_in(data@1, keys@1)
  end

  @spec put_elem(tuple(), non_neg_integer(), term()) :: tuple()
  def put_elem(tuple@1, index@1, value@1) do
    :erlang.setelement(index@1 + 1, tuple@1, value@1)
  end

  @spec put_in(Access.t(), [term(), ...], term()) :: Access.t()
  def put_in(data@1, keys@1, value@1) do
    :erlang.element(2, Kernel.get_and_update_in(data@1, keys@1, fn _ ->
        {nil, value@1}
    end))
  end

  @spec rem(integer(), (neg_integer() | pos_integer())) :: integer()
  def rem(dividend@1, divisor@1) do
    rem(dividend@1, divisor@1)
  end

  @spec round(value) :: value when value: integer()
  @spec round(unknown_type) :: integer()
  def round(number@1) do
    :erlang.round(number@1)
  end

  @spec self() :: pid()
  def self() do
    :erlang.self()
  end

  @spec send(dest :: (pid() | port() | atom() | {atom(), node()}), msg) :: msg when msg: any()
  def send(dest@1, msg@1) do
    :erlang.send(dest@1, msg@1)
  end

  @spec spawn((() -> any())) :: pid()
  def spawn(fun@1) do
    :erlang.spawn(fun@1)
  end

  @spec spawn(module(), atom(), []) :: pid()
  def spawn(module@1, fun@1, args@1) do
    :erlang.spawn(module@1, fun@1, args@1)
  end

  @spec spawn_link((() -> any())) :: pid()
  def spawn_link(fun@1) do
    :erlang.spawn_link(fun@1)
  end

  @spec spawn_link(module(), atom(), []) :: pid()
  def spawn_link(module@1, fun@1, args@1) do
    :erlang.spawn_link(module@1, fun@1, args@1)
  end

  @spec spawn_monitor((() -> any())) :: {pid(), reference()}
  def spawn_monitor(fun@1) do
    :erlang.spawn_monitor(fun@1)
  end

  @spec spawn_monitor(module(), atom(), []) :: {pid(), reference()}
  def spawn_monitor(module@1, fun@1, args@1) do
    :erlang.spawn_monitor(module@1, fun@1, args@1)
  end

  def struct(x0@1) do
    struct(x0@1, [])
  end

  @spec struct((module() | :elixir.struct()), Enum.t()) :: :elixir.struct()
  def struct(struct@1, kv@1) do
    struct(struct@1, kv@1, fn {key@1, val@1}, acc@1 ->
        case :maps.is_key(key@1, acc@1) do
          true ->
            key@1 != :__struct__
          false ->
            false
          _@1 ->
            :erlang.error({:badbool, :and, _@1})
        end
        |> case do
          true ->
            :maps.put(key@1, val@1, acc@1)
          false ->
            acc@1
        end
    end)
  end

  def struct!(x0@1) do
    struct!(x0@1, [])
  end

  @spec struct!((module() | :elixir.struct()), Enum.t()) :: (:elixir.struct() | no_return())
  def struct!(struct@1, kv@1) when :erlang.is_atom(struct@1) do
    struct@1.__struct__(kv@1)
  end

  def struct!(struct@1, kv@1) when :erlang.is_map(struct@1) do
    struct(struct@1, kv@1, fn {:__struct__, _}, acc@1 ->
        acc@1
      {key@1, val@1}, acc@2 ->
        :maps.update(key@1, val@1, acc@2)
    end)
  end

  @spec throw(term()) :: no_return()
  def throw(term@1) do
    :erlang.throw(term@1)
  end

  @spec tl(unknown_type) :: (maybe_improper_list(elem, tail) | tail) when elem: term(), tail: term()
  def tl(list@1) do
    :erlang.tl(list@1)
  end

  @spec trunc(unknown_type) :: integer()
  @spec trunc(value) :: value when value: integer()
  def trunc(number@1) do
    :erlang.trunc(number@1)
  end

  @spec tuple_size(tuple()) :: non_neg_integer()
  def tuple_size(tuple@1) do
    :erlang.tuple_size(tuple@1)
  end

  @spec update_in(Access.t(), [term(), ...], (term() -> term())) :: Access.t()
  def update_in(data@1, keys@1, fun@1) when :erlang.is_function(fun@1, 1) do
    :erlang.element(2, Kernel.get_and_update_in(data@1, keys@1, fn x@1 ->
        {nil, fun@1.(x@1)}
    end))
  end

  # Private Functions

  defp unquote(:"-MACRO-binding/2-fun-0-")(p0, p1, p2, p3) do
    # body not decompiled
  end

  defp unquote(:"-MACRO-in/3-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-MACRO-in/3-fun-1-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-MACRO-sigil_r/3-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-MACRO-sigil_r/3-fun-1-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-MACRO-use/3-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-MACRO-|>/3-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-do_pop_in/2-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-do_pop_in/2-fun-1-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-do_pop_in/2-fun-2-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-expand_aliases/2-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-get_and_update_in/3-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-get_and_update_in/3-fun-1-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-get_in/2-fun-0-")(p0) do
    # body not decompiled
  end

  defp unquote(:"-get_in/2-fun-1-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-in_list/3-fun-0-")(p0, p1, p2) do
    # body not decompiled
  end

  defp unquote(:"-put_in/3-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-struct!/2-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-struct/2-fun-0-")(p0, p1) do
    # body not decompiled
  end

  defp unquote(:"-update_in/3-fun-0-")(p0, p1) do
    # body not decompiled
  end

  def alias_meta({:__aliases__, meta@1, _}) do
    meta@1
  end

  def alias_meta(_) do
    []
  end

  def assert_module_scope(env@1, fun@1, arity@1) do
    case env@1 do
      %{:module => _@1} ->
        _@1
      _@1 when :erlang.is_map(_@1) ->
        :erlang.error({:badkey, :module, _@1})
      _@1 ->
        _@1.module()
    end
    |> case do
      nil ->
        :erlang.error(ArgumentError.exception(<<"cannot invoke ", case fun@1 do
          _@2 when :erlang.is_binary(_@2) ->
            _@2
          _@3 ->
            String.Chars.to_string(_@3)
        end :: binary, "/", case arity@1 do
          _@4 when :erlang.is_binary(_@4) ->
            _@4
          _@5 ->
            String.Chars.to_string(_@5)
        end :: binary, " outside module">>))
      _ ->
        :ok
    end
  end

  def assert_no_function_scope(env@1, fun@1, arity@1) do
    case env@1 do
      %{:function => _@1} ->
        _@1
      _@1 when :erlang.is_map(_@1) ->
        :erlang.error({:badkey, :function, _@1})
      _@1 ->
        _@1.function()
    end
    |> case do
      nil ->
        :ok
      _ ->
        :erlang.error(ArgumentError.exception(<<"cannot invoke ", case fun@1 do
          _@2 when :erlang.is_binary(_@2) ->
            _@2
          _@3 ->
            String.Chars.to_string(_@3)
        end :: binary, "/", case arity@1 do
          _@4 when :erlang.is_binary(_@4) ->
            _@4
          _@5 ->
            String.Chars.to_string(_@5)
        end :: binary, " inside function/macro">>))
    end
  end

  def bootstrapped?(_) do
    true
  end

  def build_if(condition@1, [{:do, do_clause@1}]) do
    build_if(condition@1, [{:do, do_clause@1}, {:else, nil}])
  end

  def build_if(condition@1, [{:do, do_clause@1}, {:else, else_clause@1}]) do
    optimize_boolean({:case, [], [condition@1, [{:do, [{:"->", [], [[{:when, [], [{:x, [], Kernel}, {:in, [{:context, Kernel}, {:import, Kernel}], [{:x, [], Kernel}, [false, nil]]}]}], else_clause@1]}, {:"->", [], [[{:_, [], Kernel}], do_clause@1]}]}]]})
  end

  def build_if(_condition@1, _arguments@1) do
    :erlang.error(ArgumentError.exception(<<"invalid or duplicate keys for if, only \"do\" ", "and an optional \"else\" are permitted">>))
  end

  def build_unless(condition@1, [{:do, do_clause@1}]) do
    build_unless(condition@1, [{:do, do_clause@1}, {:else, nil}])
  end

  def build_unless(condition@1, [{:do, do_clause@1}, {:else, else_clause@1}]) do
    {:if, [{:context, Kernel}, {:import, Kernel}], [condition@1, [{:do, else_clause@1}, {:else, do_clause@1}]]}
  end

  def build_unless(_condition@1, _arguments@1) do
    :erlang.error(ArgumentError.exception(<<"invalid or duplicate keys for unless, only \"do\" ", "and an optional \"else\" are permitted">>))
  end

  def comp(left@1, {:|, _, [h@1, t@1]}) do
    {{:".", [], [:erlang, :or]}, [], [{{:".", [], [:erlang, :"=:="]}, [], [left@1, h@1]}, {:in, [{:context, Kernel}, {:import, Kernel}], [left@1, t@1]}]}
  end

  def comp(left@1, right@1) do
    {{:".", [], [:erlang, :"=:="]}, [], [left@1, right@1]}
  end

  def decreasing_compare(var@1, first@1, last@1) do
    {:and, [{:context, Kernel}, {:import, Kernel}], [{{:".", [], [:erlang, :"=<"]}, [], [var@1, first@1]}, {{:".", [], [:erlang, :>=]}, [], [var@1, last@1]}]}
  end

  def define(kind@1, call@1, expr@1, env@1) do
    assert_module_scope(env@1, kind@1, 2)
    assert_no_function_scope(env@1, kind@1, 2)
    line@1 = case env@1 do
      %{:line => _@1} ->
        _@1
      _@1 when :erlang.is_map(_@1) ->
        :erlang.error({:badkey, :line, _@1})
      _@1 ->
        _@1.line()
    end
    {call@2, unquoted_call@1} = :elixir_quote.escape(call@1, true)
    {expr@2, unquoted_expr@1} = :elixir_quote.escape(expr@1, true)
    check_clauses@1 = notcase unquoted_expr@1 do
      true ->
        true
      false ->
        unquoted_call@1
      _@2 ->
        :erlang.error({:badbool, :or, _@2})
    end
    pos@1 = :elixir_locals.cache_env(env@1)
    {{:".", [], [:elixir_def, :store_definition]}, [], [line@1, kind@1, check_clauses@1, call@2, expr@2, pos@1]}
  end

  def do_at([arg@1], meta@1, name@1, function?@1, env@1) do
    line@1 = case :lists.keymember(:context, 1, meta@1) do
      true ->
        nil
      false ->
        case env@1 do
          %{:line => _@1} ->
            _@1
          _@1 when :erlang.is_map(_@1) ->
            :erlang.error({:badkey, :line, _@1})
          _@1 ->
            _@1.line()
        end
    end
    case function?@1 do
      _@2 when _@2 != nil and _@2 != false ->
        _@10 = :erlang.error(ArgumentError.exception(<<"cannot set attribute @", case name@1 do
          _@3 when :erlang.is_binary(_@3) ->
            _@3
          _@4 ->
            String.Chars.to_string(_@4)
        end :: binary, " inside function/macro">>))
        {stack@1, arg@2} = {nil, arg@1}
        _@10
      _ ->
        case name@1 == :behavior do
          true ->
            _@9 = :elixir_errors.warn(case env@1 do
              %{:line => _@5} ->
                _@5
              _@5 when :erlang.is_map(_@5) ->
                :erlang.error({:badkey, :line, _@5})
              _@5 ->
                _@5.line()
            end, case env@1 do
              %{:file => _@6} ->
                _@6
              _@6 when :erlang.is_map(_@6) ->
                :erlang.error({:badkey, :file, _@6})
              _@6 ->
                _@6.file()
            end, "@behavior attribute is not supported, please use @behaviour instead")
            {stack@1, arg@2} = {nil, arg@1}
            _@9
          false ->
            case :lists.member(name@1, [:moduledoc, :typedoc, :doc]) do
              _@7 when _@7 != nil and _@7 != false ->
                {stack@1, _} = :elixir_quote.escape(env_stacktrace(env@1), false)
                arg@2 = {case env@1 do
                  %{:line => _@8} ->
                    _@8
                  _@8 when :erlang.is_map(_@8) ->
                    :erlang.error({:badkey, :line, _@8})
                  _@8 ->
                    _@8.line()
                end, arg@1}
                {{:".", [], [{:__aliases__, [{:alias, false}], [:"Module"]}, :put_attribute]}, [], [{:__MODULE__, [], Kernel}, name@1, arg@2, stack@1, line@1]}
              _ ->
                {stack@1, arg@2} = {nil, arg@1}
                {{:".", [], [{:__aliases__, [{:alias, false}], [:"Module"]}, :put_attribute]}, [], [{:__MODULE__, [], Kernel}, name@1, arg@1, nil, line@1]}
            end
        end
    end
  end

  def do_at(args@1, _meta@1, name@1, function?@1, env@1) when :erlang.is_atom(args@1) or args@1 == [] do
    stack@1 = env_stacktrace(env@1)
    doc_attr?@1 = :lists.member(name@1, [:moduledoc, :typedoc, :doc])
    case function?@1 do
      true ->
        value@1 = Module.get_attribute(case env@1 do
          %{:module => _@1} ->
            _@1
          _@1 when :erlang.is_map(_@1) ->
            :erlang.error({:badkey, :module, _@1})
          _@1 ->
            _@1.module()
        end, name@1, stack@1)
        |> case do
          {_, doc@1} when doc_attr?@1 ->
            doc@1
          _@2 ->
            _@2
        end
        _@7 = try do
          :elixir_quote.escape(value@1, false)
        catch
          {:error, _@3@1, _} when _@3@1 == :badarg or :erlang.tuple_size(_@3@1) == 2 and :erlang.element(1, _@3@1) == :badarg ->
            ex@1 = Exception.normalize(:error, _@3@1)
            :erlang.error(ArgumentError.exception(<<<<"cannot inject attribute @", case name@1 do
              _@5 when :erlang.is_binary(_@5) ->
                _@5
              _@6 ->
                String.Chars.to_string(_@6)
            end :: binary, " into function/macro because ">> :: binary, Exception.message(ex@1) :: binary>>))
          {:error, %{:__struct__ => _@4@1, :__exception__ => true} = _@3@1, _} when _@4@1 == ArgumentError ->
            ex@1 = Exception.normalize(:error, _@3@1)
            :erlang.error(ArgumentError.exception(<<<<"cannot inject attribute @", case name@1 do
              _@5 when :erlang.is_binary(_@5) ->
                _@5
              _@6 ->
                String.Chars.to_string(_@6)
            end :: binary, " into function/macro because ">> :: binary, Exception.message(ex@1) :: binary>>))
        else
          {val@1, _} ->
            val@1
        end
        escaped@1 = nil
        _@7
      false ->
        {escaped@1, _} = :elixir_quote.escape(stack@1, false)
        value@1 = nil
        {:with, [], [{:<-, [], [{:when, [], [{{:_, [], Kernel}, {:doc, [], Kernel}}, doc_attr?@1]}, {{:".", [], [{:__aliases__, [{:alias, false}], [:"Module"]}, :get_attribute]}, [], [{:__MODULE__, [], Kernel}, name@1, escaped@1]}]}, [{:do, {:doc, [], Kernel}}]]}
    end
  end

  def do_at(args@1, _meta@1, name@1, _function?@1, _env@1) do
    :erlang.error(ArgumentError.exception(<<"expected 0 or 1 argument for @", case name@1 do
      _@1 when :erlang.is_binary(_@1) ->
        _@1
      _@2 ->
        String.Chars.to_string(_@2)
    end :: binary, ", got: ", case :erlang.length(args@1) do
      _@3 when :erlang.is_binary(_@3) ->
        _@3
      _@4 ->
        String.Chars.to_string(_@4)
    end :: binary>>))
  end

  def do_pop_in(nil, [_ | _]) do
    :pop
  end

  def do_pop_in(data@1, [h@1]) when :erlang.is_function(h@1) do
    h@1.(:get_and_update, data@1, fn _ ->
        :pop
    end)
  end

  def do_pop_in(data@1, [h@1 | t@1]) when :erlang.is_function(h@1) do
    h@1.(:get_and_update, data@1, fn _@1 ->
        do_pop_in(_@1, t@1)
    end)
  end

  def do_pop_in(data@1, [h@1]) do
    Access.pop(data@1, h@1)
  end

  def do_pop_in(data@1, [h@1 | t@1]) do
    Access.get_and_update(data@1, h@1, fn _@1 ->
        do_pop_in(_@1, t@1)
    end)
  end

  def env_stacktrace(env@1) do
    case bootstrapped?(Path) do
      true ->
        Macro.Env.stacktrace(env@1)
      false ->
        []
    end
  end

  def expand_aliases({{:".", _, [base@1, :"{}"]}, _, refs@1}, env@1) do
    base@2 = Macro.expand(base@1, env@1)
    Enum.map(refs@1, fn {:__aliases__, _, ref@1} ->
        Module.concat([base@2 | ref@1])
      ref@2 when :erlang.is_atom(ref@2) ->
        Module.concat(base@2, ref@2)
      other@1 ->
        other@1
    end)
  end

  def expand_aliases(module@1, env@1) do
    [Macro.expand(module@1, env@1)]
  end

  def expand_module(raw@1, _module@1, _env@1) when :erlang.is_atom(raw@1) do
    raw@1
  end

  def expand_module({:__aliases__, _, [:"Elixir" | t@1]}, module@1, _env@1) when t@1 != [] do
    module@1
  end

  def expand_module({:__aliases__, _, _}, module@1, %{:module => nil}) do
    module@1
  end

  def expand_module({:__aliases__, _, t@1}, _module@1, env@1) do
    :elixir_aliases.concat([case env@1 do
      %{:module => _@1} ->
        _@1
      _@1 when :erlang.is_map(_@1) ->
        :erlang.error({:badkey, :module, _@1})
      _@1 ->
        _@1.module()
    end | t@1])
  end

  def expand_module(_raw@1, module@1, env@1) do
    :elixir_aliases.concat([case env@1 do
      %{:module => _@1} ->
        _@1
      _@1 when :erlang.is_map(_@1) ->
        :erlang.error({:badkey, :module, _@1})
      _@1 ->
        _@1.module()
    end, module@1])
  end

  def extract_concatenations({:<>, _, [left@1, right@1]}) do
    [wrap_concatenation(left@1) | extract_concatenations(right@1)]
  end

  def extract_concatenations(other@1) do
    [wrap_concatenation(other@1)]
  end

  def in_list(left@1, h@1, t@1) do
    :lists.foldr(fn x@1, acc@1 ->
        {{:".", [], [:erlang, :or]}, [], [comp(left@1, x@1), acc@1]}
    end, comp(left@1, h@1), t@1)
  end

  def in_range(left@1, first@1, last@1) do
    case :erlang.is_integer(first@1) do
      true ->
        :erlang.is_integer(last@1)
      false ->
        false
    end
    |> case do
      true ->
        in_range_literal(left@1, first@1, last@1)
      false ->
        {:and, [{:context, Kernel}, {:import, Kernel}], [{:and, [{:context, Kernel}, {:import, Kernel}], [{:and, [{:context, Kernel}, {:import, Kernel}], [{{:".", [], [:erlang, :is_integer]}, [], [left@1]}, {{:".", [], [:erlang, :is_integer]}, [], [first@1]}]}, {{:".", [], [:erlang, :is_integer]}, [], [last@1]}]}, {:or, [{:context, Kernel}, {:import, Kernel}], [{:and, [{:context, Kernel}, {:import, Kernel}], [{{:".", [], [:erlang, :"=<"]}, [], [first@1, last@1]}, increasing_compare(left@1, first@1, last@1)]}, {:and, [{:context, Kernel}, {:import, Kernel}], [{{:".", [], [:erlang, :<]}, [], [last@1, first@1]}, decreasing_compare(left@1, first@1, last@1)]}]}]}
    end
  end

  def in_range_literal(left@1, first@1, ^first@1) do
    {{:".", [], [:erlang, :"=:="]}, [], [left@1, first@1]}
  end

  def in_range_literal(left@1, first@1, last@1) when first@1 < last@1 do
    {:and, [{:context, Kernel}, {:import, Kernel}], [{{:".", [], [:erlang, :is_integer]}, [], [left@1]}, increasing_compare(left@1, first@1, last@1)]}
  end

  def in_range_literal(left@1, first@1, last@1) do
    {:and, [{:context, Kernel}, {:import, Kernel}], [{{:".", [], [:erlang, :is_integer]}, [], [left@1]}, decreasing_compare(left@1, first@1, last@1)]}
  end

  def in_var(false, ast@1, fun@1) do
    fun@1.(ast@1)
  end

  def in_var(true, {atom@1, _, context@1} = var@1, fun@1) when :erlang.is_atom(atom@1) and :erlang.is_atom(context@1) do
    fun@1.(var@1)
  end

  def in_var(true, ast@1, fun@1) do
    {:__block__, [], [{:=, [], [{:var, [], Kernel}, ast@1]}, fun@1.({:var, [], Kernel})]}
  end

  def increasing_compare(var@1, first@1, last@1) do
    {:and, [{:context, Kernel}, {:import, Kernel}], [{{:".", [], [:erlang, :>=]}, [], [var@1, first@1]}, {{:".", [], [:erlang, :"=<"]}, [], [var@1, last@1]}]}
  end

  def module_nesting(nil, full@1) do
    {nil, full@1}
  end

  def module_nesting(prefix@1, full@1) do
    case split_module(prefix@1) do
      [] ->
        {nil, full@1}
      prefix@2 ->
        module_nesting(prefix@2, split_module(full@1), [], full@1)
    end
  end

  def module_nesting([x@1 | t1@1], [^x@1 | t2@1], acc@1, full@1) do
    module_nesting(t1@1, t2@1, [x@1 | acc@1], full@1)
  end

  def module_nesting([], [h@1 | _], acc@1, _full@1) do
    {:erlang.binary_to_atom(<<"Elixir.", h@1 :: binary>>, :utf8), :elixir_aliases.concat(:lists.reverse([h@1 | acc@1]))}
  end

  def module_nesting(_, _, _acc@1, full@1) do
    {nil, full@1}
  end

  def module_vars([{key@1, kind@1} | vars@1], counter@1) do
    var@1 = case :erlang.is_atom(kind@1) do
      true ->
        {key@1, [{:generated, true}], kind@1}
      false ->
        {key@1, [{:counter, kind@1}, {:generated, true}], nil}
    end
    under@1 = :erlang.binary_to_atom(<<"_@", :erlang.integer_to_binary(counter@1) :: binary>>, :utf8)
    args@1 = [key@1, kind@1, under@1, var@1]
    [{:"{}", [], args@1} | module_vars(vars@1, counter@1 + 1)]
  end

  def module_vars([], _counter@1) do
    []
  end

  def nest_get_and_update_in([], fun@1) do
    fun@1
  end

  def nest_get_and_update_in(list@1, fun@1) do
    {:fn, [], [{:"->", [], [[{:x, [], Kernel}], nest_get_and_update_in({:x, [], Kernel}, list@1, fun@1)]}]}
  end

  def nest_get_and_update_in(h@1, [{:access, key@1} | t@1], fun@1) do
    {{:".", [], [{:__aliases__, [{:alias, false}], [:"Access"]}, :get_and_update]}, [], [h@1, key@1, nest_get_and_update_in(t@1, fun@1)]}
  end

  def nest_get_and_update_in(h@1, [{:map, key@1} | t@1], fun@1) do
    {{:".", [], [{:__aliases__, [{:alias, false}], [:"Map"]}, :get_and_update!]}, [], [h@1, key@1, nest_get_and_update_in(t@1, fun@1)]}
  end

  def nest_pop_in(kind@1, list@1) do
    {:fn, [], [{:"->", [], [[{:x, [], Kernel}], nest_pop_in(kind@1, {:x, [], Kernel}, list@1)]}]}
  end

  def nest_pop_in(:map, h@1, [{:access, key@1}]) do
    {:case, [], [h@1, [{:do, [{:"->", [], [[nil], {nil, nil}]}, {:"->", [], [[{:h, [], Kernel}], {{:".", [], [{:__aliases__, [{:alias, false}], [:"Access"]}, :pop]}, [], [{:h, [], Kernel}, key@1]}]}]}]]}
  end

  def nest_pop_in(_, _, [{:map, key@1}]) do
    :erlang.error(ArgumentError.exception(<<"cannot use pop_in when the last segment is a map/struct field. ", <<"This would effectively remove the field ", Kernel.inspect(key@1) :: binary, " from the map/struct">> :: binary>>))
  end

  def nest_pop_in(_, h@1, [{:map, key@1} | t@1]) do
    {{:".", [], [{:__aliases__, [{:alias, false}], [:"Map"]}, :get_and_update!]}, [], [h@1, key@1, nest_pop_in(:map, t@1)]}
  end

  def nest_pop_in(_, h@1, [{:access, key@1}]) do
    {:case, [], [h@1, [{:do, [{:"->", [], [[nil], :pop]}, {:"->", [], [[{:h, [], Kernel}], {{:".", [], [{:__aliases__, [{:alias, false}], [:"Access"]}, :pop]}, [], [{:h, [], Kernel}, key@1]}]}]}]]}
  end

  def nest_pop_in(_, h@1, [{:access, key@1} | t@1]) do
    {{:".", [], [{:__aliases__, [{:alias, false}], [:"Access"]}, :get_and_update]}, [], [h@1, key@1, nest_pop_in(:access, t@1)]}
  end

  def nest_update_in([], fun@1) do
    fun@1
  end

  def nest_update_in(list@1, fun@1) do
    {:fn, [], [{:"->", [], [[{:x, [], Kernel}], nest_update_in({:x, [], Kernel}, list@1, fun@1)]}]}
  end

  def nest_update_in(h@1, [{:map, key@1} | t@1], fun@1) do
    {{:".", [], [{:__aliases__, [{:alias, false}], [:"Map"]}, :update!]}, [], [h@1, key@1, nest_update_in(t@1, fun@1)]}
  end

  def optimize_boolean({:case, meta@1, args@1}) do
    {:case, [{:optimize_boolean, true} | meta@1], args@1}
  end

  def proper_start?({{:".", _, [expr@1, _]}, _, _args@1}) when :erlang.is_atom(expr@1) or :erlang.element(1, expr@1) == :__aliases__ or :erlang.element(1, expr@1) == :__MODULE__ do
    true
  end

  def proper_start?({atom@1, _, _args@1}) when :erlang.is_atom(atom@1) do
    true
  end

  def proper_start?(other@1) do
    not:erlang.is_tuple(other@1)
  end

  def split_module(atom@1) do
    case :binary.split(:erlang.atom_to_binary(atom@1, :utf8), ".", [:global]) do
      ["Elixir" | t@1] ->
        t@1
      _ ->
        []
    end
  end

  def split_words(string@1, []) do
    split_words(string@1, [115])
  end

  def split_words(string@1, [mod@1]) when mod@1 == 115 or mod@1 == 97 or mod@1 == 99 do
    case :erlang.is_binary(string@1) do
      true ->
        parts@1 = String.split(string@1)
        _@1 = case mod@1 do
          115 ->
            parts@1
          97 ->
            :lists.map(:erlang.make_fun(String, :to_atom, 1), parts@1)
          99 ->
            :lists.map(:erlang.make_fun(String, :to_charlist, 1), parts@1)
        end
        parts@2 = parts@1
        _@1
      false ->
        parts@2 = {{:".", [], [{:__aliases__, [{:alias, false}], [:"String"]}, :split]}, [], [string@1]}
        case mod@1 do
          115 ->
            parts@2
          97 ->
            {{:".", [], [:lists, :map]}, [], [{:&, [], [{:/, [{:context, Kernel}, {:import, Kernel}], [{{:".", [], [{:__aliases__, [{:alias, false}], [:"String"]}, :to_atom]}, [], []}, 1]}]}, parts@2]}
          99 ->
            {{:".", [], [:lists, :map]}, [], [{:&, [], [{:/, [{:context, Kernel}, {:import, Kernel}], [{{:".", [], [{:__aliases__, [{:alias, false}], [:"String"]}, :to_charlist]}, [], []}, 1]}]}, parts@2]}
        end
    end
  end

  def split_words(_string@1, _mods@1) do
    :erlang.error(ArgumentError.exception("modifier must be one of: s, a, c"))
  end

  def struct(struct@1, [], _fun@1) when :erlang.is_atom(struct@1) do
    case struct@1 do
      %{:__struct__ => _@1} ->
        _@1
      _@1 when :erlang.is_map(_@1) ->
        :erlang.error({:badkey, :__struct__, _@1})
      _@1 ->
        _@1.__struct__()
    end
  end

  def struct(struct@1, kv@1, fun@1) when :erlang.is_atom(struct@1) do
    struct(case struct@1 do
      %{:__struct__ => _@1} ->
        _@1
      _@1 when :erlang.is_map(_@1) ->
        :erlang.error({:badkey, :__struct__, _@1})
      _@1 ->
        _@1.__struct__()
    end, kv@1, fun@1)
  end

  def struct(%{:__struct__ => _} = struct@1, [], _fun@1) do
    struct@1
  end

  def struct(%{:__struct__ => _} = struct@1, kv@1, fun@1) do
    Enum.reduce(kv@1, struct@1, fun@1)
  end

  def typespec(:type) do
    :deftype
  end

  def typespec(:typep) do
    :deftypep
  end

  def typespec(:opaque) do
    :defopaque
  end

  def typespec(:spec) do
    :defspec
  end

  def typespec(:callback) do
    :defcallback
  end

  def typespec(:macrocallback) do
    :defmacrocallback
  end

  def typespec(:optional_callbacks) do
    :defoptional_callbacks
  end

  def typespec(_) do
    false
  end

  def unnest({{:".", _, [Access, :get]}, _, [expr@1, key@1]}, acc@1, _all_map?@1, kind@1) do
    unnest(expr@1, [{:access, key@1} | acc@1], false, kind@1)
  end

  def unnest({{:".", _, [expr@1, key@1]}, _, []}, acc@1, all_map?@1, kind@1) when :erlang.is_tuple(expr@1) and :erlang.element(1, expr@1) != :__aliases__ and :erlang.element(1, expr@1) != :__MODULE__ do
    unnest(expr@1, [{:map, key@1} | acc@1], all_map?@1, kind@1)
  end

  def unnest(other@1, [], _all_map?@1, kind@1) do
    :erlang.error(ArgumentError.exception(<<"expected expression given to ", case kind@1 do
      _@1 when :erlang.is_binary(_@1) ->
        _@1
      _@2 ->
        String.Chars.to_string(_@2)
    end :: binary, " to access at least one element, got: ", case Macro.to_string(other@1) do
      _@3 when :erlang.is_binary(_@3) ->
        _@3
      _@4 ->
        String.Chars.to_string(_@4)
    end :: binary>>))
  end

  def unnest(other@1, acc@1, all_map?@1, kind@1) do
    case proper_start?(other@1) do
      true ->
        {[other@1 | acc@1], all_map?@1}
      false ->
        :erlang.error(ArgumentError.exception(<<<<"expression given to ", case kind@1 do
          _@1 when :erlang.is_binary(_@1) ->
            _@1
          _@2 ->
            String.Chars.to_string(_@2)
        end :: binary, " must start with a variable, local or remote call ">> :: binary, <<"and be followed by an element access, got: ", case Macro.to_string(other@1) do
          _@3 when :erlang.is_binary(_@3) ->
            _@3
          _@4 ->
            String.Chars.to_string(_@4)
        end :: binary>> :: binary>>))
    end
  end

  def wrap_binding(true, var@1) do
    {:^, [], [var@1]}
  end

  def wrap_binding(_, var@1) do
    var@1
  end

  def wrap_concatenation(binary@1) when :erlang.is_binary(binary@1) do
    binary@1
  end

  def wrap_concatenation(other@1) do
    {:::, [], [other@1, {:binary, [], nil}]}
  end
end
