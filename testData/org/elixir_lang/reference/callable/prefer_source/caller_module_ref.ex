defmodule CallerModuleRef do
  @moduledoc "References the Code module"
  @code_module <caret>Code

  def example do
    @code_module.string_to_quoted("1 + 2")
  end
end
