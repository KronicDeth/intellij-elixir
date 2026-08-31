defmodule UseInjectedAttributeMacro do
  defmacro __using__(_) do
    quote do
      @from_macro "Module attribute from macro"
    end
  end
end

defmodule UseInjectedAttributeUser do
  use UseInjectedAttributeMacro

  def read_keyword, do: @from_macro

  def read_block do
    @from_<caret>macro
  end
end
