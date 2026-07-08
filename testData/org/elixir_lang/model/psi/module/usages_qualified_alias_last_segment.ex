defmodule MyApp.Mod<caret>ule do
end

defmodule Usage do
  alias MyApp.Module
  use MyApp.Module
  import MyApp.Module
end
