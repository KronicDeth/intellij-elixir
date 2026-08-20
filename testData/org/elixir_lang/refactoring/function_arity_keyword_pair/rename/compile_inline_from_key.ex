defmodule KeywordPairInline do
  @compile inline: [per<caret>form: 0]

  def perform do
    :ok
  end
end
