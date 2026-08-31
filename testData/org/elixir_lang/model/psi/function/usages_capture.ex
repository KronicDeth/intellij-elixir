defmodule UsagesCapture do
  def per<caret>form(data) do
    data
  end
end

defmodule UsagesCaptureCaller do
  def run, do: &UsagesCapture.perform/1
end
