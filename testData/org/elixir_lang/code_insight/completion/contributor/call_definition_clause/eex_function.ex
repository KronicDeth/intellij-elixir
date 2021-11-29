defmodule EExFunction do
  require EEx

  EEx.function_from_file(:def, :function_from_file_sample, "sample.eex", [:a, :b])
  EEx.function_from_string(:def, :function_from_string_sample, "<%= a + b %>", [:a, :b])

  def usage do
    f<caret>
  end
end
