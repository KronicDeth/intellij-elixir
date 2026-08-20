defmodule First do
  @module_<caret>attribute 1

  def usage, do: @module_attribute
end

defmodule Second do
  @module_attribute 2

  def usage, do: @module_attribute
end
