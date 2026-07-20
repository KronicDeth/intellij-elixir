defmodule UnresolvableModuleType do
  @spec convert(Missing.thing()) :: :ok
  def convert(_value), do: :ok
end
