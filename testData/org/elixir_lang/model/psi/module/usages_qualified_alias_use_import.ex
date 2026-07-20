defmodule My<caret>App.Module do
end

defmodule Usage do
  alias MyApp.Module
  use MyApp.Module
  import MyApp.Module
end
