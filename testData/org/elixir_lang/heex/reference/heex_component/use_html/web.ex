defmodule MyAppWeb do
  def html do
    quote do
      import MyAppWeb.CoreComponents
    end
  end

  defmacro __using__(which) when is_atom(which) do
    apply(__MODULE__, which, [])
  end
end
