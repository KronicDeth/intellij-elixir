defmodule BuiltinTypes do
  @type an_integer :: integer()
  @type a_term :: term()
  @spec run(integer()) :: :ok
  def run(_value), do: :ok
end
