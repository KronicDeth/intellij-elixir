defmodule Stack do
  def start_link(args), do: {:ok, args}
end

defmodule Usage do
  @child_spec %{start: {Stack, :start_<caret>link, [[:hello]]}}
end
