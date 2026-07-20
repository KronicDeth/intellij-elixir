defmodule UsesMissingLocalType do
  @spec convert(<error descr="Type 'nope/0' is not defined">nope</error>()) :: :ok
  def convert(_value), do: :ok
end
