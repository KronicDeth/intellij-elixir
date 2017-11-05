defmodule Enum do
  def split(enumerable, count) when count < 0 do
    split_reverse_list(reverse(enumerable), - count, [])
  end
end
