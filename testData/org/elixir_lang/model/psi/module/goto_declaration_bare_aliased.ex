defmodule MyApp.Module do
end

defmodule Usage do
  alias MyApp.Module

  def go do
    Mod<caret>ule
  end
end
