defmodule MixGeneratorEmbed do
  require Mix.Generator

  Mix.Generator.embed_template(:log, "Log: <%= @log %>")
  Mix.Generator.embed_text(:error, "There was an error!")

  def usage do
    e<caret>
  end
end
