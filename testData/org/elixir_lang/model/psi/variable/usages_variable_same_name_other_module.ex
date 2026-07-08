defmodule VariableTarget do
  def run do
    va<caret>riable = 1
    variable
  end
end

defmodule VariableOther do
  def run do
    variable = 2
    variable
  end
end
