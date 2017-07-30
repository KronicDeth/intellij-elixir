defmodule One do
  def two(
        three,
        %{
          four: five,
          six: seven
        }
      ) do
    %{
      ^five => %Eight{
        nine_id: nine_id
      },
      ^seven => %Nine{
        id: nine<caret>_id
      }
    } = three

    nine_id
  end
end
