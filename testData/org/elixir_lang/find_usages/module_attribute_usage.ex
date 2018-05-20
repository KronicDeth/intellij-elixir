defmodule ModuleAttribute do
  @module_attribute 1

  def usage, do: @module_<caret>attribute
end
