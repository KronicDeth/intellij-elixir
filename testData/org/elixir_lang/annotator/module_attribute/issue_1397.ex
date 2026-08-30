defmodule Formatter do
  @callback format(String.()) :: String.t()
  @callback parse(String.(integer)) :: String.t()
end
