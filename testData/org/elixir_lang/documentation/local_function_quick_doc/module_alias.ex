defmodule Callee do
  @moduledoc "Callee module documentation."
  def multiply(a, b) do
    a * b
  end
end

defmodule Caller do
  def run do
    Call<caret>ee.multiply(2, 3)
  end
end
