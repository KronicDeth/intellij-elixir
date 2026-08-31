defmodule BeamCaptureGoto do
  def run do
    &:queue.ne<caret>w/0
  end
end
