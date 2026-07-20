defmodule ParameterInfo.LocalCall do
  def run do
    add(<caret>)
  end

  def add(augend, addend), do: augend + addend
end
