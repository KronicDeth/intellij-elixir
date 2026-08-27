defmodule MyComponentLibrary do
  defmacro __using__(opts \\ []) do
    conditional =
      if __CALLER__.module != MyComponentLibrary.Helpers do
        quote do: import(MyComponentLibrary.Helpers)
      end

    imports =
      quote bind_quoted: [opts: opts] do
        import MyComponentLibrary
      end

    [conditional, imports]
  end

  def link(assigns) do
    ~H""
  end

  def live_title(assigns) do
    ~H""
  end
end
