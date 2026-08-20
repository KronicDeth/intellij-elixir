defmodule KeywordPairExporter do
  def per<caret>form, do: :ok
  def other, do: :ok
end

defmodule KeywordPairImporter do
  import KeywordPairExporter, except: [perform: 0]

  def run, do: other()
end
