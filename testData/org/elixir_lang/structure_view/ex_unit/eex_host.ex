defmodule EExHost do
  require EEx

  EEx.function_from_file(:def, :render_from_file, "greeting.eex", [:assigns])

  EEx.function_from_string(:def, :render_from_string, "<%= @name %>", [:assigns])
end
