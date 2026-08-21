defmodule MyAppWeb do
  def html do
    quote do
      use MyComponentLibrary

      unquote(html_helpers())
    end
  end

  defp html_helpers do
    quote do
      import MyAppWeb.CoreComponents
    end
  end

  defmacro __using__(which) when is_atom(which) do
    apply(__MODULE__, which, [])
  end
end
