defmodule UsesMissingType do
  @spec convert(Other.<error descr="Type 'Other.missing/0' is not defined">missing</error>()) :: :ok
  def convert(_value), do: :ok
end
