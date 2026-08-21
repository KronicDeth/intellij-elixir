defmodule MyComponentLibrary do
  defmacro __using__(_opts) do
    [
      quote do
        import MyComponentLibrary.Helpers
      end,
      quote do
        import MyComponentLibrary
      end
    ]
  end

  def link(assigns) do
    ~H""
  end
end
