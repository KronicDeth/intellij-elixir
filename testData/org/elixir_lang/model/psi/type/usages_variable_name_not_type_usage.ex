defmodule TypeUsagesVariableName do
  @type grap<caret>heme :: term()

  defp do_reverse({grapheme, rest}, acc) do
    {grapheme, rest, acc}
  end
end
