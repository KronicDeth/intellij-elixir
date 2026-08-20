defmodule UsagesQualified do
  def per<caret>form(data) do
    data
  end
end

defmodule UsagesQualifiedCaller do
  def run(value), do: UsagesQualified.perform(value)
end
