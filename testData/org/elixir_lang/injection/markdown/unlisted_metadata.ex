defmodule UnlistedMetadata do
  @doc tags: [:api]
  def tagged_function(arg) do
    arg
  end

  @doc "Adds " <> "two."
  def concatenated_doc(arg) do
    arg
  end
end
