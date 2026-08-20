# Extracted from Elixir 1.11.3 kernel.ex (defdelegate macro body, line ~5131).
# Tests that @doc keyword pairs with the `delegate_to` key do not cause SEVERE errors
# in the markdown injector.
defmodule DelegateTo do
  @doc delegate_to: {SomeModule, :some_function, 1}
  def delegated_function(arg) do
    SomeModule.some_function(arg)
  end

  @doc deprecated: "Use other_function/1 instead"
  def old_function(arg) do
    other_function(arg)
  end

  @doc since: "1.8.0"
  def versioned_function(arg) do
    arg
  end

  @doc guard: true
  def guard_function(arg) do
    arg
  end

  @doc """
  A normal docstring that should have markdown injected.
  """
  def normal_function(arg) do
    arg
  end
end
