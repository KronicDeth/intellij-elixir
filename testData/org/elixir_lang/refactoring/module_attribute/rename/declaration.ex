defmodule ModuleAttributeRename do
  @my_<caret>attribute 1

  def get do
    @my_attribute
  end
end
