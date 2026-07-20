defmodule FunctionUsagesSpecification do
  @spec perform(integer()) :: integer()
  def per<caret>form(value), do: value

  def run(value), do: perform(value)
end
