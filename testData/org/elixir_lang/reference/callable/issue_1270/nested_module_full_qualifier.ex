defmodule Autocomplete do
  defmodule State do
    def another_test do
      Autocomplete.t<caret>
    end
  end

  def test do
  end

  defp internal_test do
  end
end
