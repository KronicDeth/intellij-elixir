defprotocol CaptureConvertible do
  def convert(data)
end

defmodule CaptureRunner do
  def run do
    &CaptureConvertible.con<caret>vert/1
  end
end
