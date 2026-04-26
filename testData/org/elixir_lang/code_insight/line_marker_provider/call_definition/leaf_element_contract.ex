# Extracted from Elixir 1.11.3 kernel.ex — tests that line markers are anchored to leaf elements.
# The CallDefinition LineMarkerProvider must return LineMarkerInfo only when invoked with leaf
# elements, not composite Call or AtUnqualifiedNoParenthesesCall elements.
defmodule LeafElementContract do
  @doc """
  Returns the absolute value of `number`.
  """
  @doc guard: true
  @spec abs(number) :: number
  def abs(number) do
    :erlang.abs(number)
  end

  @doc """
  Returns the smallest integer greater than or equal to `number`.
  """
  @doc since: "1.8.0", guard: true
  @spec ceil(number) :: integer
  def ceil(number) do
    :erlang.ceil(number)
  end

  @doc """
  Performs an integer division.
  """
  @doc guard: true
  @spec div(integer, neg_integer | pos_integer) :: integer
  def div(dividend, divisor) do
    :erlang.div(dividend, divisor)
  end

  @doc """
  A private helper function.
  """
  defp helper(x) do
    x + 1
  end

  @doc """
  Another function to test separators between different function groups.
  """
  @spec other_function(any) :: any
  def other_function(x) do
    helper(x)
  end
end
