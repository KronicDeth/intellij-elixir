defmodule MyComponentLibrary do
  defmacro __using__(_opts) do
    imports =
      quote do
        import MyComponentLibrary
      end
  end

  def link(assigns) do
    ~H""
  end
end
