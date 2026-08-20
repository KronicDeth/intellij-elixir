defmodule ParameterInfo.DifferentArityDeclaration do
  def process(item), do: item
  def process(item, opts), do: {item, opts}
end
