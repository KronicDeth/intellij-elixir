defmodule CallerModuleRef do
  @shared_module <caret>Shared

  def example do
    @shared_module.helper("1 + 2")
  end
end
