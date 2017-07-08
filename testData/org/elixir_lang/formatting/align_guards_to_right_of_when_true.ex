defmodule Calcinator.Resources.Page do
  def to_params(%__MODULE__{number: number, size: size})
      when is_integer(number) and number > 0 and
           is_integer(size) and size > 0 do
    %{
      "page" => %{
        "number" => number,
        "size" => size
      }
    }
  end
end
