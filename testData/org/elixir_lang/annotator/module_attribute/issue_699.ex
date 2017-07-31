defmodule One do
  @spec area(length, width) :: integer when
  def area(length \\ 1, width \\ 1) when is_number(length) and is_number(width)
end
