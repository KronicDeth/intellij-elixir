defmodule Autocomplete do
  defmodule State do
    def another_test do
      State.t<caret>
    end
  end

  def test do
  end

  defp internal_test do
  end
end
