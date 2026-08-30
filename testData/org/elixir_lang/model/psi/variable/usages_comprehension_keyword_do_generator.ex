defmodule ComprehensionKeywordDo do
  def run(list) do
    for no<caret>de <- list, do: node + 1
  end
end
