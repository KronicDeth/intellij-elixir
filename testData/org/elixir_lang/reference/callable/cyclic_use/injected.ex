defmodule CyclicClient do
  use CyclicWeb

  def usage do
    injected_by_view<caret>
  end
end
