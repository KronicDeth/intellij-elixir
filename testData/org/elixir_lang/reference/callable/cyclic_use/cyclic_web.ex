defmodule CyclicWeb do
  defmacro __using__(_opts) do
    view(:injected)
  end

  def view(:injected) do
    quote do
      def injected_by_view(), do: :ok
    end
  end

  def view(:cyclic) do
    admin_view(:cyclic)
  end

  def admin_view(:cyclic) do
    view(:cyclic)
  end
end
