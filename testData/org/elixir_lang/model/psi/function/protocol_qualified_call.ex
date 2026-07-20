defprotocol Convertible do
  def convert(data)
end

defmodule Runner do
  def run do
    Convertible.con<caret>vert([])
  end
end
