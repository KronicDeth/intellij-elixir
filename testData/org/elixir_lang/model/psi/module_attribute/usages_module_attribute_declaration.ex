defmodule ModuleAttributeDeclaration do
  @module_<caret>attribute 1
  def before, do: @module_attribute
  @module_attribute 2

  def later, do: @module_attribute
end
