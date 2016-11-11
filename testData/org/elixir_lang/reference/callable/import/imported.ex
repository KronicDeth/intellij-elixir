defmodule Imported do
  def imported() do
    imported(1)
  end

  def unimported() do
  end

  defp imported(1) do
    :ok
  end
end
