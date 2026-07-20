defmodule MyApp.Modul<caret>e do
end

defmodule MyApp.OtherModule do
  alias MyApp.Module

  def thing, do: Module
end
