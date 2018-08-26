defmodule Decimal do
  pow10_max =
    Enum.reduce(0..104, 1, fn int, acc ->
      defp pow10(unquote(int)), do: unquote(acc)
      defp base<caret>10?(unquote(acc)), do: true
      acc * 10
    end)
end
