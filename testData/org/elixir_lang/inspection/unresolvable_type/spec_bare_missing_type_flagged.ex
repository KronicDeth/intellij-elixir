defmodule SpecBareMissingType do
  @spec convert(<error descr="Type 'a/0' is not defined">a</error>) :: :ok
  def convert(_value), do: :ok
end
