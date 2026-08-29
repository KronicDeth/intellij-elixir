defmodule Iteraptor.Iteraptable do
  @moduledoc """
  `use Iteraptor.Iteraptable` inside structs to make them both
  [`Enumerable`](http://elixir-lang.org/docs/stable/elixir/Enumerable.html) and
  [`Collectable`](http://elixir-lang.org/docs/stable/elixir/Collectable.html) and
  implement the [`Access`](https://hexdocs.pm/elixir/Access.html#content) behaviour:

  ## Usage

  Use the module within the struct of your choice and this struct will be
  automagically granted `Enumerable` and `Collectable` protocols implementations.

  `use Iteraptor.Iteraptable` accepts keyword parameter `skip: Access` or
  `skip: [Enumerable, Collectable]` which allows to implement a subset of
  protocols. Also it accepts keyword parameter `derive: MyProtocol` allowing
  to specify what protocol(s) implementations should be implicitly derived
  for this struct.
  """

  defmodule Unsupported do
    @moduledoc """
    Unsupported in applying `Iteraptor.Iteraptable`
    """
    defexception [:reason, :message]

    @doc false
    def exception(reason: reason) do
      message =
        "the given function must return a two-element tuple or :pop, got: #{inspect(reason)}"

      %Iteraptor.Iteraptable.Unsupported{message: message, reason: reason}
    end
  end

  @codepieces %{
    Enumerable =>
      quote location: :keep do
        defimpl Enumerable, for: __MODULE__ do
          def slice(enumerable) do
            {:error, __MODULE__}
          end

          def count(map) do
            # do not count :__struct__
            {:ok, map |> Map.from_struct() |> map_size}
          end

          def member?(_, {:__struct__, _}) do
            {:ok, false}
          end

          def member?(map, {key, value}) do
            {:ok, match?({:ok, ^value}, :maps.find(key, map))}
          end

          def member?(_, _) do
            {:ok, false}
          end

          def reduce(map, acc, fun) do
            do_reduce(map |> Map.from_struct() |> :maps.to_list(), acc, fun)
          end

          defp do_reduce(_, {:halt, acc}, _fun), do: {:halted, acc}

          defp do_reduce(list, {:suspend, acc}, fun),
            do: {:suspended, acc, &do_reduce(list, &1, fun)}

          defp do_reduce([], {:cont, acc}, _fun), do: {:done, acc}
          defp do_reduce([h | t], {:cont, acc}, fun), do: do_reduce(t, fun.(h, acc), fun)
        end
      end,
    Collectable =>
      quote location: :keep do
        defimpl Collectable, for: __MODULE__ do
          def into(original) do
            {original,
             fn
               map, {:cont, {k, v}} -> :maps.put(k, v, map)
               map, :done -> map
               _, :halt -> :ok
             end}
          end
        end
      end,
    Access =>
      quote location: :keep do
        @behaviour Access

        @impl Access
        def fetch(term, key), do: Map.fetch(term, key)

        @impl Access
        def pop(term, key, default \\ nil),
          do: {get(term, key, default), delete(term, key)}

        @impl Access
        def get_and_update(term, key, fun) when is_function(fun, 1) do
          current = get(term, key)

          case fun.(current) do
            {get, update} -> {get, put(term, key, update)}
            :pop -> {current, delete(term, key)}
            other -> raise Unsupported, reason: other
          end
        end

        if Version.compare(System.version(), "1.7.0") == :lt, do: @impl(Access)

        def get(term, key, default \\ nil) do
          case term do
            %{^key => value} -> value
            _ -> default
          end
        end

        def put(term, key, val), do: %{term | key => val}

        def delete(term, key), do: put(term, key, nil)

        defoverridable get: 3, put: 3, delete: 2
      end
  }

  @iteraptable (quote location: :keep do
                  defimpl Iteraptable, for: __MODULE__ do
                    def type(_), do: __MODULE__
                    def name(_), do: Macro.underscore(__MODULE__)
                    def to_enumerable(term), do: term
                    def to_collectable(term), do: term
                  end
                end)

  @doc """
  Allows to enable iterating features on structs with `use Iteraptor.Iteraptable`

  ## Parameters

  - keyword parameter `opts`
    - `skip: Access` or `skip: [Enumerable, Collectable]` allows
    to implement a subset of protocols;
    - `derive: MyProtocol` allows to derive selected protocol implementation(s).
  """
  defmacro __using__(opts \\ []) do
    checker = quote(location: :keep, do: @after_compile({Iteraptor.Utils, :struct_checker}))

    derive =
      opts[:derive]
      |> Macro.expand(__ENV__)
      |> case do
        nil -> []
        value when is_list(value) -> value
        value -> [value]
      end
      |> case do
        [] -> []
        protos -> [quote(location: :keep, do: @derive(unquote(protos)))]
      end

    skip =
      opts
      |> Keyword.get(:skip, [])
      |> Macro.expand(__ENV__)

    excluded =
      skip
      |> case do
        :all -> Map.keys(@codepieces)
        value when is_list(value) -> value
        value -> [value]
      end
      |> Enum.map(fn value ->
        case value |> to_string() |> String.capitalize() do
          <<"Elixir.", _::binary>> -> value
          _ -> Module.concat([value])
        end
      end)

    init =
      case [Enumerable, Collectable] -- excluded do
        [Enumerable, Collectable] -> [checker, @iteraptable | derive]
        _ -> [checker | derive]
      end

    Enum.reduce(@codepieces, init, fn {type, ast}, acc ->
      if Enum.find(excluded, &(&1 == type)), do: acc, else: [ast | acc]
    end)
  end
end
