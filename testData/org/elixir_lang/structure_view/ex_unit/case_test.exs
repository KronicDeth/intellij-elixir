defmodule CaseTest do
  use ExUnit.Case

  describe "a describe block" do
    # `describe` evaluates its block in the module body, so these are the case module's own
    # declarations. Each builds an element that casts its parent's presentation to `Parent` unguarded.
    defstruct [:a]
    @type t :: any()
    @callback a_callback() :: :ok
    @spec a_spec() :: :ok
    defdelegate values(map), to: Map

    setup do
      :ok
    end

    test "nested inside the describe" do
      assert true
    end
  end

  test "at the top level, outside any describe" do
    defmodule DefinedInsideATest do
      def helper, do: :ok
    end

    # A test's implementation, not its declarations: these must build no node.
    for n <- 1..3, do: n

    case :ok do
      :ok -> :fine
    end

    assert true
  end

  # No `do` block, so `Unknown.is` is false and nothing else matches it either.
  test "not implemented yet"

  # The one entry whose `suitable = false` flag has an observable consequence.
  true or false

  # `CallDefinitionHead.is` claims it and builds nothing; `Unknown.is` claims it and builds a node.
  unrecognised_macro() do
    :ok
  end

  test do
    assert true
  end
end
