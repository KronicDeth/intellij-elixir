def max_extent_for_position(fences, fence_i, height_n, dir) when dir == :left or dir == :right do
  range = if (dir == :left), <caret>do: (fence_i..0), else: (fence_i..map_size(fences))
  Enum.reduce_while(range, fence_i, fn(test_i, acc) ->
    if !is_empty_space(fences, test_i, height_n) do
      {:cont, test_i}
    else
      {:halt, acc}
    end
  end)
end
