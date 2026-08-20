defmodule Prefix.NoBareHeadFallbackDeclaration do
  def normalize(value, fallback) when is_binary(value), do: value
  def normalize([], fallback), do: fallback
end
