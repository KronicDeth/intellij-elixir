defmodule ParameterInfo.TrailingCommaLocalCall do
  def run do
    add(1,<caret>)
  end

  def add(augend, addend), do: augend + addend
end
