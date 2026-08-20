defmodule KeywordPairInline do
  @compile inline: [perform: 0]

  def per<caret>form do
    :ok
  end
end
