defmodule MyApp.A do
end

defmodule MyApp.B do
end

defmodule Usage do
  alias MyApp.{A, <caret>B}
end
