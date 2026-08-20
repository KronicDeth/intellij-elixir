defmodule KeywordPairInline do
  @compile {:inline, execute: 0}

  def execute do
    :ok
  end
end
