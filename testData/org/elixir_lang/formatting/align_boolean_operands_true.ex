defmodule Foo do
  def bar(value)
      when is_map(value) and
           map_size(value) > 1 and map_size(value) < 3 do
  end

  def foo(value)
      when is_binary(value) or
           is_list(value) or
           is_map(value) and map_size(value) > 0 do
  end
end
