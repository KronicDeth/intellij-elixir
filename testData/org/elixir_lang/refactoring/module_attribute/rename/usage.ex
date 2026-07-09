defmodule ModuleAttributeRename do
  @my_attribute 1

  def get do
    @my_<caret>attribute
  end
end
